# VPC 모듈
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = local.name
  cidr = var.vpc_cidr

  azs             = var.azs
  private_subnets = [
    for k, v in var.azs :
    cidrsubnet(var.vpc_cidr, var.private_subnet_prefix_extension, k)
  ]
  public_subnets = [
    for k, v in var.azs :
    cidrsubnet(var.vpc_cidr, var.public_subnet_prefix_extension, k + 48)
  ]

  enable_nat_gateway     = var.enable_nat_gateway
  one_nat_gateway_per_az = var.one_nat_gateway_per_az
  enable_dns_hostnames   = var.enable_dns_hostnames
  enable_dns_support     = var.enable_dns_support

  tags = local.tags
}

# Auto Scaling Group 모듈
module "asg" {
  source  = "terraform-aws-modules/autoscaling/aws"
  version = "~> 6.5"

  name = local.name

  min_size                  = var.asg_config.min_size
  max_size                  = var.asg_config.max_size
  desired_capacity          = var.asg_config.desired_capacity
  wait_for_capacity_timeout = 0
  health_check_type         = "EC2"
  vpc_zone_identifier       = module.vpc.private_subnets

  instance_refresh = {
    strategy = "Rolling"
    preferences = {
      checkpoint_delay       = 600
      checkpoint_percentages = [35, 70, 100]
      instance_warmup        = 300
      min_healthy_percentage = 50
    }
    triggers = ["tag"]
  }

  launch_template_name        = "${local.name}-lt"
  launch_template_description = "Launch template for ${local.name}"
  update_default_version      = true

  instance_type = var.instance_config[var.environment].instance_type
  image_id      = var.instance_config[var.environment].ami_id
  enable_monitoring = var.enable_detailed_monitoring

  block_device_mappings = var.use_ebs ? [
    {
      device_name = "/dev/sda1"
      ebs = {
        volume_size = var.ebs_config.volume_size
        volume_type = var.ebs_config.volume_type
      }
    }
  ] : []

  security_groups = [module.app_sg.security_group_id]

  user_data = base64encode(templatefile("${path.module}/user_data.sh", {
    docker_image = var.docker_image
    name         = local.name
  }))

  iam_instance_profile_name = aws_iam_instance_profile.app_profile.name

  key_name = var.ssh_key_name

  tags = local.tags
}

# Bastion 호스트
module "ec2_instance" {
  source  = "terraform-aws-modules/ec2-instance/aws"
  version = "~> 3.0"

  count = var.create_bastion ? 1 : 0

  name = "${local.name}-bastion"

  ami                    = var.instance_config[var.environment].ami_id
  instance_type          = var.instance_config[var.environment].instance_type
  key_name               = var.ssh_key_name
  monitoring             = true
  vpc_security_group_ids = [module.bastion_sg[0].security_group_id]
  subnet_id              = module.vpc.public_subnets[0]
  associate_public_ip_address = true # 퍼블릭 IP 할당 추가

  tags = local.tags
}

# CloudWatch 로그 그룹
resource "aws_cloudwatch_log_group" "app" {
  name              = "/aws/ec2/${local.name}"
  retention_in_days = var.cloudwatch_retention_days

  tags = local.tags
}
