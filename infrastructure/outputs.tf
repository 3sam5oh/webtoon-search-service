# VPC 관련 출력
output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "private_subnets" {
  description = "List of IDs of private subnets"
  value       = module.vpc.private_subnets
}

output "public_subnets" {
  description = "List of IDs of public subnets"
  value       = module.vpc.public_subnets
}

output "nat_public_ips" {
  description = "List of public Elastic IPs created for AWS NAT Gateway"
  value       = module.vpc.nat_public_ips
}

output "azs" {
  description = "A list of availability zones specified as argument to this module"
  value       = module.vpc.azs
}

# EC2 및 Auto Scaling 관련 출력
output "asg_name" {
  description = "Name of the Auto Scaling Group"
  value       = module.asg.autoscaling_group_name
}

output "asg_arn" {
  description = "ARN of the Auto Scaling Group"
  value       = module.asg.autoscaling_group_arn
}

output "launch_template_id" {
  description = "The ID of the launch template"
  value       = module.asg.launch_template_id
}

output "launch_template_name" {
  description = "The name of the launch template"
  value       = module.asg.launch_template_name
}

output "launch_template_latest_version" {
  description = "The latest version of the launch template"
  value       = module.asg.launch_template_latest_version
}

# Bastion 관련 출력
output "bastion_public_ip" {
  description = "The public IP address of the bastion host"
  value       = var.create_bastion ? module.ec2_instance[0].public_ip : null
}

output "bastion_instance_id" {
  description = "The instance ID of the bastion host"
  value       = var.create_bastion ? module.ec2_instance[0].id : null
}

# 보안 그룹 관련 출력
output "app_security_group_id" {
  description = "The ID of the security group for the application servers"
  value       = module.app_sg.security_group_id
}

output "bastion_security_group_id" {
  description = "The ID of the security group for the bastion host"
  value       = var.create_bastion ? module.bastion_sg[0].security_group_id : null
}

# IAM 관련 출력
output "app_iam_role_name" {
  description = "The name of the IAM role for the application servers"
  value       = aws_iam_role.app_role.name
}

output "app_iam_instance_profile_name" {
  description = "The name of the IAM instance profile for the application servers"
  value       = aws_iam_instance_profile.app_profile.name
}

# CloudWatch 관련 출력
output "cloudwatch_log_group_name" {
  description = "The name of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.app.name
}
