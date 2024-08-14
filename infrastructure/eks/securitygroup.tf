# securitygroup.tf


#############################
# SSM 엔드포인트 보안 그룹
#############################
module "ssm_endpoint_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-ssm-endpoint-sg"
  description = "Security group for SSM VPC Endpoints"
  vpc_id      = module.vpc.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      description = "HTTPS from VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    }
  ]

  egress_rules = ["all-all"]

  tags = local.tags
}

#############################
# OpenSearch 보안 그룹
#############################
module "opensearch_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-op-sg"
  description = "Security group for OpenSearch domain"
  vpc_id      = module.vpc.vpc_id

  ingress_with_source_security_group_id = [
    {
      from_port                = 443
      to_port                  = 443
      protocol                 = "tcp"
      description              = "HTTPS from EKS nodes"
      source_security_group_id = module.eks.node_security_group_id
    },
    {
      from_port                = 443
      to_port                  = 443
      protocol                 = "tcp"
      description              = "HTTPS from SSM"
      source_security_group_id = module.ssm_endpoint_sg.security_group_id
    },
    {
      from_port                = 9200
      to_port                  = 9200
      protocol                 = "tcp"
      description              = "OpenSearch cluster port from EKS nodes"
      source_security_group_id = module.eks_sg.security_group_id
    },
    {
      from_port                = 5601
      to_port                  = 5601
      protocol                 = "tcp"
      description              = "OpenSearch Dashboard port from EKS nodes"
      source_security_group_id = module.eks_sg.security_group_id
    }
  ]

  ingress_with_cidr_blocks = [
    {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      description = "HTTPS access from anywhere for development"
      cidr_blocks = "0.0.0.0/0"  # 개발 환경
    }
  ]

  egress_rules = ["all-all"]

  tags = local.tags
}

#############################
# EKS 클러스터 보안 그룹
#############################
module "eks_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-eks-sg"
  description = "Security group for EKS cluster and nodes"
  vpc_id      = module.vpc.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      description = "Allow HTTPS traffic within VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
    {
      from_port   = 9200
      to_port     = 9200
      protocol    = "tcp"
      description = "Allow OpenSearch cluster traffic"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
    {
      from_port   = 5601
      to_port     = 5601
      protocol    = "tcp"
      description = "Allow OpenSearch Dashboard traffic"
      cidr_blocks = module.vpc.vpc_cidr_block
    }
  ]

  egress_rules = ["all-all"]

  tags = local.tags
}

#############################
# EKS 노드에서 Prometheus, Grafana, Fluent Bit로의 접근을 허용하는 보안 그룹 설정
#############################
resource "aws_security_group" "monitoring_sg" {
  name        = "${local.name}-monitoring-sg"
  description = "Security group for monitoring tools (Prometheus, Grafana, Fluent Bit)"
  vpc_id      = module.vpc.vpc_id

  # Prometheus 접근 허용
  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    description = "Allow Prometheus traffic"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  # Grafana 접근 허용
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    description = "Allow Grafana traffic"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  # Fluent Bit 접근 허용
  ingress {
    from_port   = 24224
    to_port     = 24224
    protocol    = "tcp"
    description = "Allow Fluent Bit traffic"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  # Spring Boot 애플리케이션 접근 허용
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    description = "Allow Spring Boot traffic"
    cidr_blocks = [module.vpc.vpc_cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.tags
}

