# bastion 모듈
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

  tags = local.tags
}

# Security Group 모듈
module "app_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${local.name}-app-sg"
  description = "Security group for application servers"
  vpc_id      = module.vpc.vpc_id

  ingress_with_source_security_group_id = [
    {
      from_port                = 22
      to_port                  = 22
      protocol                 = "tcp"
      description              = "SSH access from Bastion"
      source_security_group_id = var.create_bastion ? module.bastion_sg[0].security_group_id : null
    }
  ]

  ingress_with_cidr_blocks = [
    {
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      description = "HTTP access from VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
    {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      description = "HTTPS access from VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    }
  ]

  egress_rules = ["all-all"]

  tags = local.tags
}
