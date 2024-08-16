# outputs.tf

#############################
# VPC 관련 출력
#############################
output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "vpc_cidr" {
  description = "The CIDR block of the VPC"
  value       = module.vpc.vpc_cidr_block
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

#############################
# EKS 관련 출력
#############################
output "eks_cluster_name" {
  description = "The name of the EKS cluster"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

output "eks_cluster_security_group_id" {
  description = "Security group ID attached to the cluster control plane"
  value       = module.eks.cluster_security_group_id
}

output "eks_node_security_group_id" {
  description = "Security group ID attached to the EKS nodes"
  value       = module.eks.node_security_group_id
}

output "eks_oidc_provider_arn" {
  description = "The ARN of the OIDC Provider"
  value       = module.eks.oidc_provider_arn
}

output "eks_cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data required to communicate with the cluster"
  value       = module.eks.cluster_certificate_authority_data
}

# 수정: EKS 모듈에서 생성된 노드 그룹 IAM 역할 이름 출력
output "eks_node_group_role_name" {
  description = "Name of the EKS node group IAM role"
  value       = module.eks.eks_managed_node_groups["main"].iam_role_name
}

# Fluent Bit IAM Role ARN
output "fluentbit_role_arn" {
  description = "The ARN of the IAM role for Fluent Bit"
  value       = aws_iam_role.fluentbit_role.arn
}

# Monitoring Security Group ID
output "monitoring_sg_id" {
  description = "The ID of the security group for monitoring tools"
  value       = aws_security_group.monitoring_sg.id
}

#############################
# OpenSearch 관련 출력
#############################
output "opensearch_domain_endpoint" {
  description = "Domain-specific endpoint used to submit index, search, and data upload requests to an OpenSearch domain"
  value       = aws_opensearch_domain.opensearch.endpoint
}

output "opensearch_domain_arn" {
  description = "The ARN of the OpenSearch domain"
  value       = aws_opensearch_domain.opensearch.arn
}

output "opensearch_dashboard_url" {
  description = "OpenSearch dashboard URL"
  value       = "https://${aws_opensearch_domain.opensearch.endpoint}/_dashboards/"
}

#############################
# SSM 관련 출력
#############################
output "ssm_endpoints" {
  description = "List of SSM VPC endpoint IDs"
  value       = [aws_vpc_endpoint.ssm.id, aws_vpc_endpoint.ec2messages.id, aws_vpc_endpoint.ssmmessages.id]
}

# 추가: EKS 추가 정책 ARN
output "eks_additional_policy_arn" {
  description = "ARN of the additional policy attached to EKS cluster and nodes"
  value       = aws_iam_policy.eks_additional_policy.arn
}

# 추가: OpenSearch 보안 그룹 ID
output "opensearch_security_group_id" {
  description = "ID of the security group attached to OpenSearch domain"
  value       = module.opensearch_sg.security_group_id
}

# 추가: EKS 클러스터 버전
output "eks_cluster_version" {
  description = "The Kubernetes server version of the cluster"
  value       = module.eks.cluster_version
}

# 추가: EKS 노드 그룹 설정
output "eks_node_group_config" {
  description = "EKS node group configuration"
  value = {
    min_size     = var.eks_node_group_min_size
    max_size     = var.eks_node_group_max_size
    desired_size = var.eks_node_group_desired_size
    instance_types = var.eks_node_group_instance_types
  }
}
