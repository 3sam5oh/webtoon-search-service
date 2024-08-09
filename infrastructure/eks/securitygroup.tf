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

  ingress_with_source_security_group_id = [
    {
      from_port                = 0
      to_port                  = 65535
      protocol                 = "tcp"
      description              = "Allow inbound traffic from cluster"
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
