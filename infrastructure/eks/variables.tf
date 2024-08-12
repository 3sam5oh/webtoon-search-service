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

variable "eks_node_group_ami_type" {
  description = "AMI type for the EKS node group"
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

variable "enable_irsa" {
  description = "Whether to enable IAM Roles for Service Accounts"
  type        = bool
}

#############################
# Bastion host 관련 설정
#############################
variable "create_bastion" {
  description = "Whether to create a Bastion host"
  type        = bool
}

variable "bastion_ami_id" {
  description = "AMI ID for the Bastion host"
  type        = string
}

variable "bastion_instance_type" {
  description = "Instance type for the Bastion host"
  type        = string
}

variable "bastion_key_name" {
  description = "Key pair name for the Bastion host"
  type        = string
}

variable "bastion_ingress_cidr" {
  description = "CIDR block for Bastion ingress"
  type        = string
}

#############################
# OpenSearch 관련 설정
#############################
variable "opensearch_index" {
  description = "OpenSearch index name"
  type        = string
}

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

variable "opensearch_az_count" {
  description = "Number of Availability Zones for OpenSearch"
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
}

#############################
# Nginx Ingress Controller 설정
#############################
variable "install_nginx_ingress" {
  description = "Whether to install NGINX Ingress Controller"
  type        = bool
}

variable "nginx_ingress_load_balancer_type" {
  description = "Nginx Ingress load balancer type"
  type        = string
}

#############################
# Fluent Bit 설정
#############################
variable "install_fluent_bit" {
  description = "Whether to install Fluent Bit"
  type        = bool
}

#############################
# Helm 차트 관련 설정
#############################
variable "install_ebs_csi_driver" {
  description = "Whether to install EBS CSI Driver"
  type        = bool
}

variable "install_prometheus" {
  description = "Whether to install Prometheus"
  type        = bool
}

variable "install_grafana" {
  description = "Whether to install Grafana"
  type        = bool
}

# variable "install_kube_state_metrics" {
#   description = "Whether to install Kube State Metrics"
#   type        = bool
# }
#
# variable "install_node_exporter" {
#   description = "Whether to install Node Exporter"
#   type        = bool
# }
#
# variable "install_argocd" {
#   description = "Whether to install ArgoCD"
#   type        = bool
# }

#############################
# 보안 그룹 설정
#############################
variable "eks_cluster_ingress_cidr_blocks" {
  description = "List of CIDR blocks to allow ingress to EKS cluster"
  type        = list(string)
}

variable "eks_cluster_ingress_rules" {
  description = "List of ingress rules to create for EKS cluster"
  type        = list(string)
}

#############################
# ssm 관련 설정
#############################
variable "environment_variables" {
  description = "Environment variables for EKS nodes"
  type        = map(string)
  default     = {}
}
