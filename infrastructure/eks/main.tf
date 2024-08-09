#############################
# Provider 설정
#############################
provider "aws" {
  region = var.region
}

#############################
# Helm Provider 설정
#############################
provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
    }
  }
}

#############################
# Kubernetes Provider 설정
#############################
provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
  }
}

#############################
# 로컬 변수 설정
#############################
locals {
  # 프로젝트 이름 및 환경 조합
  name = "${var.project_name}-${var.environment}"

  # 공통 태그
  tags = merge(
    var.tags,
    {
      Environment = var.environment
      Project     = var.project_name
      ManagedBy   = "Terraform"
    }
  )
}

#############################
# VPC 구성
#############################
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = local.name
  cidr = var.vpc_cidr

  azs             = var.azs
  private_subnets = [for k, v in var.azs :cidrsubnet(var.vpc_cidr, var.private_subnet_prefix_extension, k)]
  public_subnets  = [for k, v in var.azs :cidrsubnet(var.vpc_cidr, var.public_subnet_prefix_extension, k + 48)]

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
# EKS Cluster
#############################
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.13"

  cluster_name                   = "${local.name}-cluster"
  cluster_version                = var.eks_cluster_version
  cluster_endpoint_public_access = true

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  eks_managed_node_groups = {
    main = {
      min_size     = var.eks_node_group_min_size
      max_size     = var.eks_node_group_max_size
      desired_size = var.eks_node_group_desired_size

      ami_type       = var.eks_node_group_ami_type
      instance_types = var.eks_node_group_instance_types
      capacity_type  = "ON_DEMAND"

      labels = var.environment_variables

      iam_role_arn   = aws_iam_role.eks_node_group.arn
    }
  }

  # IAM 역할과 보안 그룹 참조
  iam_role_arn                     = aws_iam_role.eks_cluster.arn
  node_security_group_id           = module.eks_node_sg.security_group_id
  cluster_security_group_id        = module.eks_cluster_sg.security_group_id

  enable_irsa = var.enable_irsa

  tags = local.tags
}

#############################
# Bastion 호스트
#############################
resource "aws_instance" "bastion" {
  count                  = var.create_bastion ? 1 : 0
  ami                    = var.bastion_ami_id
  instance_type          = var.bastion_instance_type
  key_name               = var.bastion_key_name
  subnet_id              = module.vpc.public_subnets[0]
  vpc_security_group_ids = [module.bastion_sg[0].security_group_id]
  iam_instance_profile   = aws_iam_instance_profile.bastion_profile[0].name

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-bastion"
    }
  )
}

# #############################
# # Nginx Ingress Controller
# #############################
# resource "helm_release" "nginx_ingress" {
#   count            = var.install_nginx_ingress ? 1 : 0
#   name             = "nginx-ingress"
#   repository       = "https://kubernetes.github.io/ingress-nginx"
#   chart            = "ingress-nginx"
#   namespace        = "ingress-nginx"
#   create_namespace = true
#
#   set {
#     name  = "controller.service.type"
#     value = "LoadBalancer"
#   }
#
#   depends_on = [module.eks]
# }
#
# #############################
# # Fluent Bit
# #############################
# resource "helm_release" "fluent_bit" {
#   count            = var.install_fluent_bit ? 1 : 0
#   name             = "fluent-bit"
#   repository       = "https://fluent.github.io/helm-charts"
#   chart            = "fluent-bit"
#   namespace        = "logging"
#   create_namespace = true
#
#   set {
#     name  = "config.outputs"
#     value = "opensearch" // OpenSearch로 로그를 전송하도록 설정
#   }
#
#   set {
#     name  = "config.opensearch.host"
#     value = var.opensearch_endpoint
#   }
#
#   set {
#     name  = "config.opensearch.index"
#     value = var.opensearch_index
#   }
#
#   depends_on = [module.eks]
# }
#
# #############################
# # Metrics 배포를 위한 Helm 차트
# #############################
# resource "helm_release" "kube_state_metrics" {
#   count            = var.install_kube_state_metrics ? 1 : 0
#   name             = "kube-state-metrics"
#   repository       = "https://prometheus-community.github.io/helm-charts"
#   chart            = "kube-state-metrics"
#   namespace        = "monitoring"
#   create_namespace = true
#
#   depends_on = [module.eks]
# }
#
# #############################
# # Node-exporter
# #############################
# resource "helm_release" "node_exporter" {
#   count            = var.install_node_exporter ? 1 : 0
#   name             = "node-exporter"
#   repository       = "https://prometheus-community.github.io/helm-charts"
#   chart            = "prometheus-node-exporter"
#   namespace        = "monitoring"
#   create_namespace = true
#
#   depends_on = [module.eks]
# }
#
# #############################
# # EBS CSI Driver Helm 차트
# #############################
# resource "helm_release" "aws_ebs_csi_driver" {
#   count            = var.install_ebs_csi_driver ? 1 : 0
#   name             = "aws-ebs-csi-driver"
#   repository       = "https://kubernetes-sigs.github.io/aws-ebs-csi-driver"
#   chart            = "aws-ebs-csi-driver"
#   namespace        = "kube-system"
#   create_namespace = false
#
#   set {
#     name  = "controller.serviceAccount.create"
#     value = "true"
#   }
#
#   set {
#     name  = "controller.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
#     value = aws_iam_role.ebs_csi_driver.arn
#   }
#
#   depends_on = [module.eks, aws_iam_role.ebs_csi_driver]
# }
#
# #############################
# # Prometheus
# #############################
# resource "helm_release" "prometheus" {
#   count            = var.install_prometheus ? 1 : 0
#   name             = "prometheus"
#   repository       = "https://prometheus-community.github.io/helm-charts"
#   chart            = "prometheus"
#   namespace        = "monitoring"
#   create_namespace = true
#
#   depends_on = [module.eks]
# }
#
# #############################
# # Grafana
# #############################
# resource "helm_release" "grafana" {
#   count            = var.install_grafana ? 1 : 0
#   name             = "grafana"
#   repository       = "https://grafana.github.io/helm-charts"
#   chart            = "grafana"
#   namespace        = "monitoring"
#   create_namespace = true
#
#   depends_on = [module.eks]
# }
#
# #############################
# # ArgoCD
# #############################
# resource "helm_release" "argocd" {
#   count            = var.install_argocd ? 1 : 0
#   name             = "argocd"
#   repository       = "https://argoproj.github.io/argo-helm"
#   chart            = "argo-cd"
#   namespace        = "argocd"
#   create_namespace = true
#
#   depends_on = [module.eks]
# }
