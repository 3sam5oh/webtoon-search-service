#############################
# 기본 설정
#############################
variable "region" {
  description = "AWS region"
  type        = string
}

variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
}

#############################
# VPC 관련 설정
#############################
variable "vpc_cidr" {
  description = "VPC CIDR"
  type        = string
}

variable "azs" {
  description = "Availability Zones"
  type        = list(string)
}

variable "private_subnet_prefix_extension" {
  description = "CIDR block bits extension to calculate CIDR blocks of each private subnetwork"
  type        = number
}

variable "public_subnet_prefix_extension" {
  description = "CIDR block bits extension to calculate CIDR blocks of each public subnetwork"
  type        = number
}

variable "enable_nat_gateway" {
  description = "Enable NAT gateway"
  type        = bool
}

variable "one_nat_gateway_per_az" {
  description = "Use one NAT gateway per availability zone"
  type        = bool
}

variable "enable_dns_hostnames" {
  description = "Enable DNS hostnames in VPC"
  type        = bool
}

variable "enable_dns_support" {
  description = "Enable DNS support in VPC"
  type        = bool
}

#############################
# EC2 및 Auto Scaling 관련 설정
#############################
variable "instance_config" {
  description = "EC2 instance configuration for different environments"
  type = map(object({
    instance_type = string
    ami_id        = string
  }))
}

variable "asg_config" {
  description = "Auto Scaling Group configuration"
  type = object({
    min_size         = number
    max_size         = number
    desired_capacity = number
  })
}

variable "docker_image" {
  description = "Docker image to run on EC2 instances"
  type        = string
}

variable "enable_detailed_monitoring" {
  description = "Enable detailed monitoring for EC2 instances"
  type        = bool
}

variable "ssh_key_name" {
  description = "Name of the SSH key pair to use for EC2 instances"
  type        = string
}

#############################
# Bastion 관련 설정
#############################
variable "create_bastion" {
  description = "Whether to create a bastion host"
  type        = bool
}

variable "bastion_ingress_cidr" {
  description = "CIDR block for SSH access to bastion"
  type        = string
}

# variable "ebs_config" {
#   description = "EBS volume configuration"
#   type = object({
#     volume_size = number
#     volume_type = string
#   })
# }

#############################
# CloudWatch 관련 설정
#############################
variable "cloudwatch_retention_days" {
  description = "Number of days to retain CloudWatch logs"
  type        = number
}
