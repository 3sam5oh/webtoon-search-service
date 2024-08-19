# variables.tf

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
# EKS 관련 설정
#############################
variable "eks_cluster_version" {
  description = "Kubernetes version to use for the EKS cluster"
  type        = string
}

variable "eks_node_group_instance_types" {
  description = "List of instance types for the EKS node group"
  type        = list(string)
}

variable "eks_node_group_min_size" {
  description = "Minimum number of nodes in the EKS node group"
  type        = number
}

variable "eks_node_group_max_size" {
  description = "Maximum number of nodes in the EKS node group"
  type        = number
}

variable "eks_node_group_desired_size" {
  description = "Desired number of nodes in the EKS node group"
  type        = number
}

#############################
# OpenSearch 관련 설정
#############################
variable "opensearch_engine_version" {
  description = "OpenSearch engine version"
  type        = string
}

variable "opensearch_instance_type" {
  description = "OpenSearch instance type"
  type        = string
}

variable "opensearch_instance_count" {
  description = "OpenSearch instance count"
  type        = number
}

variable "opensearch_volume_type" {
  description = "OpenSearch EBS volume type"
  type        = string
}

variable "opensearch_volume_size" {
  description = "OpenSearch EBS volume size (GiB)"
  type        = number
}

variable "opensearch_master_user_name" {
  description = "OpenSearch master user name"
  type        = string
}

variable "opensearch_master_user_password" {
  description = "OpenSearch master user password"
  type        = string
  sensitive   = true
}

#############################
# 기타 설정
#############################
variable "environment_variables" {
  description = "Environment variables for EKS nodes"
  type        = map(string)
  default     = {}
}
