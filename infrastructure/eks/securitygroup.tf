# securitygroup.tf

#############################
# Bastion 보안 그룹 (조건부)
#############################
module "bastion_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"
  count   = var.create_bastion ? 1 : 0

  name        = "${local.name}-bastion-sg"
  description = "Security group for bastion host"
  vpc_id      = module.vpc.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      description = "SSH access"
      cidr_blocks = var.bastion_ingress_cidr
    }
  ]

  egress_rules = ["all-all"]

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-bastion-sg"
    }
  )
}

#############################
# EKS 클러스터 보안 그룹
#############################
module "eks_cluster_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-eks-cluster-sg"
  description = "Security group for EKS cluster"
  vpc_id      = module.vpc.vpc_id

  ingress_cidr_blocks = var.eks_cluster_ingress_cidr_blocks
  ingress_rules       = var.eks_cluster_ingress_rules

  egress_rules = ["all-all"]

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-eks-cluster-sg"
    }
  )
}

#############################
# EKS 노드 그룹 보안 그룹
#############################
module "eks_node_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-eks-node-sg"
  description = "Security group for EKS node group"
  vpc_id      = module.vpc.vpc_id

  ingress_with_self = [
    {
      rule        = "all-all"
      description = "Allow all internal traffic between nodes"
    }
  ]

  ingress_with_source_security_group_id = [
    {
      from_port                = 10250
      to_port                  = 10250
      protocol                 = "tcp"
      description              = "Allow kubelet traffic from cluster"
      source_security_group_id = module.eks_cluster_sg.security_group_id
    },
    {
      from_port                = 22
      to_port                  = 22
      protocol                 = "tcp"
      description              = "SSH access from Bastion"
      source_security_group_id = var.create_bastion ? module.bastion_sg[0].security_group_id : null
    }
  ]

  egress_rules = ["all-all"]

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-eks-node-sg"
    }
  )
}

#############################
# OpenSearch 보안 그룹
#############################
module "opensearch_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-opensearch-sg"
  description = "Security group for OpenSearch domain"
  vpc_id      = module.vpc.vpc_id

  ingress_with_source_security_group_id = [
    {
      from_port                = 443
      to_port                  = 443
      protocol                 = "tcp"
      description              = "HTTPS from EKS nodes"
      source_security_group_id = module.eks_node_sg.security_group_id
    }
  ]

  egress_rules = ["all-all"]

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-opensearch-sg"
    }
  )
}

#############################
# NLB 보안 그룹 (Nginx Ingress Controller)
#############################
module "nlb_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-nlb-sg"
  description = "Security group for Network Load Balancer"
  vpc_id      = module.vpc.vpc_id

  ingress_cidr_blocks = ["0.0.0.0/0"]
  ingress_rules       = ["http-80-tcp", "https-443-tcp"]

  egress_rules = ["all-all"]

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-nlb-sg"
    }
  )
}

#############################
# EKS 노드 그룹에 NLB 접근 허용
#############################
resource "aws_security_group_rule" "nodes_to_nlb" {
  type                     = "ingress"
  from_port                = 30000
  to_port                  = 32767
  protocol                 = "tcp"
  security_group_id        = module.eks_node_sg.security_group_id
  source_security_group_id = module.nlb_sg.security_group_id
  description              = "Allow NLB access to NodePort services"
}
