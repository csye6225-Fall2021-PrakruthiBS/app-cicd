variable "domain_tld" {
  type        = string
  description = "domain_tld"
}
variable "AWS_ACCOUNT_ID" {
  type        = string
  description = "AWS_ACCOUNT_ID"
}
variable "codedeploy_bucket_name" {
  type        = string
  description = "codedeploy_bucket_name"
}
variable "CODE_DEPLOY_APPLICATION_NAME" {
  type        = string
  description = "CODE_DEPLOY_APPLICATION_NAME"
}
variable "iam_policy_arn_GHaction_app" {
  description = "IAM Policies to be attached to githubactions"
  type        = list(string)
}
variable "iam_custom_policy_codedeploy_ec2_s3" {
  description = "IAM Policy to be attached to role"
  type        = string
}
variable "iam_ghactions_app_user" {
  description = "IAM User for github actions"
  type        = string
}

variable "regionName" {
  type        = string
  description = "region"
}

variable "profileName" {
  type        = string
  description = "Profile name"
}

variable "accessKeyId" {
  type        = string
  description = "Profile name"
}

variable "secretAccessKey" {
  type        = string
  description = "Profile name"
}

variable "CodeDeploy-EC2-S3" {
  type        = string
  description = "CodeDeploy-EC2-S3"
}

variable "GH-Code-Deploy" {
  type        = string
  description = "GH-Code-Deploy"
}

variable "GH-Upload-To-S3" {
  type        = string
  description = "GH-Upload-To-S3"
}