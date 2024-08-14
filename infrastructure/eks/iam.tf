# iam.tf

#############################
# SSM OpenSearch 접근 정책
#############################
resource "aws_iam_policy" "ssm_opensearch_access" {
  name        = "${local.name}-ssm-opensearch-access"
  path        = "/"
  description = "IAM policy for accessing OpenSearch via SSM Session Manager"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssm:StartSession",
          "ssm:TerminateSession",
          "ssm:ResumeSession",
          "ssm:DescribeSessions",
          "ssm:GetConnectionStatus"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "es:ESHttp*"
        ]
        Resource = aws_opensearch_domain.opensearch.arn
      }
    ]
  })
}

#############################
# EKS Cluster IAM Role
#############################
resource "aws_iam_role" "eks_cluster_role" {
  name = "${local.name}-eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster_role.name
}

resource "aws_iam_role_policy_attachment" "eks_vpc_resource_controller" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
  role       = aws_iam_role.eks_cluster_role.name
}

#############################
# EKS 노드 그룹 역할
#############################
resource "aws_iam_role" "eks_node_group_role" {
  name = "${local.name}-eks-node"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "eks_node_group_policy_attachment" {
  for_each = {
    worker_node = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
    cni_policy  = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
    ecr_policy  = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
    ssm_policy  = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
  }

  policy_arn = each.value
  role       = aws_iam_role.eks_node_group_role.name
}

resource "aws_iam_role_policy_attachment" "eks_nodes_ssm_opensearch_policy" {
  policy_arn = aws_iam_policy.ssm_opensearch_access.arn
  role       = aws_iam_role.eks_node_group_role.name
}

#############################
# OpenSearch 관리자 역할
#############################
resource "aws_iam_role" "opensearch_admin_role" {
  name = "${local.name}-opensearch-admin-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "eks_node_opensearch_access" {
  policy_arn = aws_iam_policy.ssm_opensearch_access.arn
  role       = aws_iam_role.eks_node_group_role.name
}

resource "aws_iam_role_policy_attachment" "opensearch_admin_policy" {
  policy_arn = aws_iam_policy.ssm_opensearch_access.arn
  role       = aws_iam_role.opensearch_admin_role.name
}

#############################
# Fluent Bit IAM Role
#############################
resource "aws_iam_role" "fluentbit_role" {
  name = "${local.name}-fluentbit-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = local.tags
}

# Fluent Bit이 OpenSearch에 접근할 수 있도록 정책을 붙임
resource "aws_iam_role_policy_attachment" "fluentbit_opensearch_policy" {
  role       = aws_iam_role.fluentbit_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonOpenSearchServiceFullAccess"
}

#############################
# Prometheus & Grafana IAM Role
#############################
resource "aws_iam_role" "prometheus_grafana_role" {
  name = "${local.name}-prometheus-grafana-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = local.tags
}

# Prometheus와 Grafana가 필요로 하는 정책을 붙임
resource "aws_iam_role_policy_attachment" "prometheus_policy" {
  role       = aws_iam_role.prometheus_grafana_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonPrometheusFullAccess"
}

resource "aws_iam_role_policy_attachment" "grafana_policy" {
  role       = aws_iam_role.prometheus_grafana_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonGrafanaFullAccess"
}
