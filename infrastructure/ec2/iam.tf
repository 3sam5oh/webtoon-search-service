#############################
# IAM Role
#############################
resource "aws_iam_role" "app_role" {
  name = "${local.name}-app-role"

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

#############################
# IAM Instance Profile
#############################
resource "aws_iam_instance_profile" "app_profile" {
  name = "${local.name}-app-profile"
  role = aws_iam_role.app_role.name
}

#############################
# IAM Policy Attachments
#############################
resource "aws_iam_role_policy_attachment" "cloudwatch_policy" {
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
  role       = aws_iam_role.app_role.name
}

# 필요에 따라 추가 정책 첨부 가능
# 예: SSM 정책
# resource "aws_iam_role_policy_attachment" "ssm_policy" {
#   policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
#   role       = aws_iam_role.app_role.name
# }
