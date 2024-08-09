locals {
  # 프로젝트 이름 및 환경 조합
  name = "${var.project_name}-${var.environment}"

  # 공통 태그
  tags = merge(
    var.tags,
    {
      Environment = var.environment
      Project     = var.project_name
      ManagedBy   = "Terraform"
    }
  )
}
