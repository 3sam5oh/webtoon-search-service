# main.tf

#############################
# Provider 설정
#############################
provider "aws" {
  region = var.region
}

data "aws_caller_identity" "current" {}

provider "kubernetes" {
  host = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)

  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args = [
      "eks", "get-token", "--cluster-name", module.eks.cluster_name, "--region", local.region
    ]
  }
}

provider "helm" {
  kubernetes {
    host = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)

    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args = [
        "eks", "get-token", "--cluster-name", module.eks.cluster_name, "--region", local.region
      ]
    }
  }
}

#############################
# 로컬 변수 설정
#############################
locals {
  name   = "${var.project_name}-${var.environment}"
  region = var.region

  vpc_cidr = var.vpc_cidr
  azs      = var.azs

  tags = var.tags
}

#############################
# VPC 구성
#############################
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = local.name
  cidr = local.vpc_cidr

  azs             = local.azs
  private_subnets = [for k, v in local.azs :cidrsubnet(local.vpc_cidr, var.private_subnet_prefix_extension, k)]
  public_subnets  = [for k, v in local.azs :cidrsubnet(local.vpc_cidr, var.public_subnet_prefix_extension, k + 48)]

  enable_nat_gateway     = var.enable_nat_gateway
  one_nat_gateway_per_az = var.one_nat_gateway_per_az
  enable_dns_hostnames   = var.enable_dns_hostnames
  enable_dns_support     = var.enable_dns_support

  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
  }

  tags = local.tags
}

#############################
# SSM VPC 엔드포인트
#############################
resource "aws_vpc_endpoint" "ssm" {
  vpc_id            = module.vpc.vpc_id
  service_name      = "com.amazonaws.${var.region}.ssm"
  vpc_endpoint_type = "Interface"
  subnet_ids        = module.vpc.private_subnets
  security_group_ids = [module.ssm_endpoint_sg.security_group_id]
}

resource "aws_vpc_endpoint" "ec2messages" {
  vpc_id            = module.vpc.vpc_id
  service_name      = "com.amazonaws.${var.region}.ec2messages"
  vpc_endpoint_type = "Interface"
  subnet_ids        = module.vpc.private_subnets
  security_group_ids = [module.ssm_endpoint_sg.security_group_id]
}

resource "aws_vpc_endpoint" "ssmmessages" {
  vpc_id            = module.vpc.vpc_id
  service_name      = "com.amazonaws.${var.region}.ssmmessages"
  vpc_endpoint_type = "Interface"
  subnet_ids        = module.vpc.private_subnets
  security_group_ids = [module.ssm_endpoint_sg.security_group_id]
}

#############################
# EKS Cluster
#############################
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.13"

  cluster_name    = "${local.name}-cluster"
  cluster_version = var.eks_cluster_version

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  cluster_endpoint_public_access = true

  create_cloudwatch_log_group = false

#   # 보안 그룹 설정
#   create_cluster_security_group = false
#   create_node_security_group    = false
#   cluster_security_group_id     = module.eks_sg.security_group_id
#   node_security_group_id        = module.eks_sg.security_group_id

  # IAM 역할 설정
  create_iam_role = false
  iam_role_arn    = aws_iam_role.eks_cluster_role.arn

  # 관리형 노드 그룹 설정
  eks_managed_node_groups = {
    main = {
      name = "${local.name}-eks-node"

      min_size     = var.eks_node_group_min_size
      max_size     = var.eks_node_group_max_size
      desired_size = var.eks_node_group_desired_size

      instance_types = var.eks_node_group_instance_types
      capacity_type  = "ON_DEMAND"

      create_iam_role = false
      iam_role_arn    = aws_iam_role.eks_node_group_role.arn

#       create_security_group          = false
#       vpc_security_group_ids         = [module.eks_sg.security_group_id]
#
#       use_name_prefix                = false

      labels = var.environment_variables

      tags = merge(local.tags, {
        "Name" = "${local.name}-eks-node"
      })
    }
  }

#   # aws-auth configmap 관리
#   manage_aws_auth_configmap = true
#   aws_auth_roles = [
#     {
#       rolearn  = aws_iam_role.eks_node_group_role.arn
#       username = "system:node:{{EC2PrivateDNSName}}"
#       groups   = ["system:bootstrappers", "system:nodes"]
#     },
#   ]

  tags = local.tags
}

#############################
# OpenSearch 도메인 생성
#############################
resource "aws_opensearch_domain" "opensearch" {
  domain_name    = "${local.name}-op"
  engine_version = var.opensearch_engine_version

  cluster_config {
    instance_type = var.opensearch_instance_type
    instance_count = var.opensearch_instance_count

    # Multi-AZ 설정 추가
    zone_awareness_enabled = false
    #     zone_awareness_config {
    #       availability_zone_count = 2
    #     }

    # Dedicated master node 설정 추가
    dedicated_master_enabled = false
    #     dedicated_master_type = "t3.medium.search"
    #     dedicated_master_count = 3
  }

  ebs_options {
    ebs_enabled = true
    volume_type = var.opensearch_volume_type
    volume_size = var.opensearch_volume_size
  }

  vpc_options {
    subnet_ids = [module.vpc.private_subnets[0]]
    security_group_ids = [module.opensearch_sg.security_group_id]
  }

  encrypt_at_rest {
    enabled = true
  }

  node_to_node_encryption {
    enabled = true
  }

  domain_endpoint_options {
    enforce_https           = true
    tls_security_policy     = "Policy-Min-TLS-1-2-2019-07"
    custom_endpoint_enabled = false
  }

  advanced_security_options {
    enabled = true
    internal_user_database_enabled = true
    master_user_options {
      master_user_name     = var.opensearch_master_user_name
      master_user_password = var.opensearch_master_user_password
    }
  }

  #   # Auto-Tune 설정 추가
  #   auto_tune_options {
  #     desired_state = "ENABLED"
  #   }

  tags = local.tags
}

resource "aws_opensearch_domain_policy" "opensearch_domain_policy" {
  domain_name = aws_opensearch_domain.opensearch.domain_name

  access_policies = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = "*"
        Action = "es:*"
        Resource = "${aws_opensearch_domain.opensearch.arn}/*"
      }
    ]
  })
}

