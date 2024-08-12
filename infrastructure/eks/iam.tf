# iam.tf

#############################
# Bastion 호스트 역할
#############################
resource "aws_iam_role" "bastion_role" {
  count = var.create_bastion ? 1 : 0
  name  = "${local.name}-bastion-role"

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

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-bastion-role"
    }
  )
}

resource "aws_iam_role_policy_attachment" "bastion_ssm_policy_attachment" {
  count      = var.create_bastion ? 1 : 0
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
  role       = aws_iam_role.bastion_role[0].name
}

resource "aws_iam_instance_profile" "bastion_instance_profile" {
  count = var.create_bastion ? 1 : 0
  name  = "${local.name}-bastion-profile"
  role  = aws_iam_role.bastion_role[0].name

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-bastion-profile"
    }
  )
}

#############################
# EKS 클러스터 역할
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

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-eks-cluster-role"
    }
  )
}

resource "aws_iam_role_policy_attachment" "eks_cluster_policy_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster_role.name
}

#############################
# EKS 노드 그룹 역할
#############################
resource "aws_iam_role" "eks_node_group_role" {
  name = "${local.name}-eks-node-group-role"

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

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-eks-node-group-role"
    }
  )
}

resource "aws_iam_role_policy_attachment" "eks_node_group_policy_attachment" {
  for_each = {
    worker_node   = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
    cni_policy    = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
    ssm_policy    = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
    nlb_access    = aws_iam_policy.nlb_management_policy.arn
  }

  policy_arn = each.value
  role       = aws_iam_role.eks_node_group_role.name
}

#############################
# NLB 관리를 위한 커스텀 정책
#############################
resource "aws_iam_policy" "nlb_management_policy" {
  name        = "${local.name}-nlb-management-policy"
  path        = "/"
  description = "IAM policy for managing NLB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "elasticloadbalancing:DescribeLoadBalancers",
          "elasticloadbalancing:DescribeTargetGroups",
          "elasticloadbalancing:DescribeTargetHealth",
          "elasticloadbalancing:CreateLoadBalancer",
          "elasticloadbalancing:CreateTargetGroup",
          "elasticloadbalancing:DeleteLoadBalancer",
          "elasticloadbalancing:DeleteTargetGroup",
          "elasticloadbalancing:ModifyLoadBalancerAttributes",
          "elasticloadbalancing:ModifyTargetGroup",
          "elasticloadbalancing:ModifyTargetGroupAttributes",
          "elasticloadbalancing:RegisterTargets",
          "elasticloadbalancing:DeregisterTargets"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ec2:DescribeInstances",
          "ec2:DescribeSubnets",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeAddresses",
          "ec2:DescribeInternetGateways"
        ]
        Resource = "*"
      }
    ]
  })
}

#############################
# Nginx Ingress Controller IAM 역할
#############################
resource "aws_iam_role" "nginx_ingress_role" {
  name = "${local.name}-nginx-ingress-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = module.eks.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${module.eks.oidc_provider}:sub": "system:serviceaccount:ingress-nginx:nginx-ingress-serviceaccount"
          }
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "nginx_ingress_policy_attachment" {
  policy_arn = aws_iam_policy.nlb_management_policy.arn
  role       = aws_iam_role.nginx_ingress_role.name
}

#############################
# EBS CSI Driver IAM 역할 (후에 PVC가 필요할 경우 바로 설치 할 수 있도록 준비)
#############################
resource "aws_iam_role" "ebs_csi_driver_role" {
  name = "${local.name}-ebs-csi-driver-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = module.eks.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${module.eks.oidc_provider}:sub" : "system:serviceaccount:kube-system:ebs-csi-controller-sa"
          }
        }
      }
    ]
  })

  tags = merge(
    local.tags,
    {
      Name = "${local.name}-ebs-csi-driver-role"
    }
  )
}

resource "aws_iam_role_policy_attachment" "ebs_csi_driver_policy_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy"
  role       = aws_iam_role.ebs_csi_driver_role.name
}

#############################
# Fluent Bit OpenSearch 접근을 위한 IAM 정책
#############################
resource "aws_iam_policy" "fluentbit_opensearch_policy" {
  name        = "${local.name}-fluentbit-opensearch-access"
  description = "IAM policy to allow Fluent Bit to access OpenSearch"
  policy      = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = [
          "es:ESHttpPost",
          "es:ESHttpPut",
          "es:ESHttpGet"
        ]
        Resource = "${aws_opensearch_domain.opensearch.arn}/*"
      }
    ]
  })
}

resource "aws_iam_role" "fluentbit_opensearch_role" {
  name = "${local.name}-fluentbit-opensearch-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = module.eks.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${module.eks.oidc_provider}:sub": "system:serviceaccount:logging:fluent-bit"
          }
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "fluentbit_opensearch_policy_attachment" {
  policy_arn = aws_iam_policy.fluentbit_opensearch_policy.arn
  role       = aws_iam_role.fluentbit_opensearch_role.name
}

#############################
# Prometheus IAM 역할 및 정책
#############################
resource "aws_iam_role" "prometheus_role" {
  name = "${local.name}-prometheus-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = module.eks.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${module.eks.oidc_provider}:sub": "system:serviceaccount:monitoring:prometheus"
          }
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_policy" "prometheus_policy" {
  name        = "${local.name}-prometheus-policy"
  description = "IAM policy for Prometheus"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ec2:Describe*",
          "elasticloadbalancing:Describe*",
          "autoscaling:Describe*",
          "cloudwatch:GetMetricData",
          "cloudwatch:ListMetrics",
          "cloudwatch:GetMetricStatistics",
          "eks:ListClusters",
          "eks:DescribeCluster",
          "tag:GetResources",
          "rds:DescribeDBInstances",
          "rds:ListTagsForResource",
          "sqs:ListQueues",
          "sqs:GetQueueAttributes",
          "sns:ListTopics",
          "sns:ListSubscriptions",
          "elasticache:DescribeCacheClusters",
          "elasticache:ListTagsForResource"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "prometheus_policy_attachment" {
  policy_arn = aws_iam_policy.prometheus_policy.arn
  role       = aws_iam_role.prometheus_role.name
}

#############################
# Grafana IAM 역할 및 정책
#############################
resource "aws_iam_role" "grafana_role" {
  name = "${local.name}-grafana-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = module.eks.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${module.eks.oidc_provider}:sub": "system:serviceaccount:monitoring:grafana"
          }
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_policy" "grafana_policy" {
  name        = "${local.name}-grafana-policy"
  description = "IAM policy for Grafana"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ec2:DescribeInstances",
          "ec2:DescribeRegions",
          "cloudwatch:GetMetricData",
          "cloudwatch:ListMetrics",
          "cloudwatch:GetMetricStatistics",
          "cloudwatch:GetDashboard",
          "cloudwatch:ListDashboards",
          "eks:ListClusters",
          "eks:DescribeCluster",
          "rds:DescribeDBInstances",
          "tag:GetResources",
          "ce:GetCostAndUsage",
          "ce:GetCostForecast",
          "s3:ListAllMyBuckets",
          "s3:GetBucketLocation",
          "s3:GetBucketTagging"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "grafana_policy_attachment" {
  policy_arn = aws_iam_policy.grafana_policy.arn
  role       = aws_iam_role.grafana_role.name
}
