resource "aws_iam_policy" "CodeDeploy-EC2-S3" {
  name   = var.CodeDeploy-EC2-S3
  path   = "/"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:AbortMultipartUpload",
                "s3:ListBucket",
                "s3:DeleteObject",
                "s3:GetObjectVersion",
                "s3:ListMultipartUploadParts"
            ],
            "Effect": "Allow",
            "Resource": [
              "arn:aws:s3:::${var.codedeploy_bucket_name}/*",
                "arn:aws:s3:::${var.codedeploy_bucket_name}"
              ]
        }
    ]
}
EOF
}

resource "aws_iam_policy" "GH-Code-Deploy" {
  name   = var.GH-Code-Deploy
  path   = "/"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "codedeploy:RegisterApplicationRevision",
        "codedeploy:GetApplicationRevision"
      ],
      "Resource": [
        "arn:aws:codedeploy:${var.regionName}:${var.AWS_ACCOUNT_ID}:application:${var.CODE_DEPLOY_APPLICATION_NAME}"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "codedeploy:CreateDeployment",
        "codedeploy:GetDeployment"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "codedeploy:GetDeploymentConfig"
      ],
      "Resource": [
        "arn:aws:codedeploy:${var.regionName}:${var.AWS_ACCOUNT_ID}:deploymentconfig:CodeDeployDefault.OneAtATime",
        "arn:aws:codedeploy:${var.regionName}:${var.AWS_ACCOUNT_ID}:deploymentconfig:CodeDeployDefault.HalfAtATime",
        "arn:aws:codedeploy:${var.regionName}:${var.AWS_ACCOUNT_ID}:deploymentconfig:CodeDeployDefault.AllAtOnce"
      ]
    }
  ]
}
EOF
}

resource "aws_iam_policy" "GH-Upload-To-S3" {
  name   = var.GH-Upload-To-S3
  path   = "/"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
      {
          "Effect": "Allow",
          "Action": [
              "s3:PutObject",
              "s3:GetObject",
              "s3:List*"
          ],
          "Resource": [
              "arn:aws:s3:::${var.codedeploy_bucket_name}"
          ]
      }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "role-policy-attachment" {
  role       = "EC2-CSYE6225"
  policy_arn = aws_iam_policy.CodeDeploy-EC2-S3.arn
}

resource "aws_iam_user_policy_attachment" "ghactions-app-GH-Code-Deploy-policy-attachment" {
  user       = var.iam_ghactions_app_user
  policy_arn = aws_iam_policy.GH-Code-Deploy.arn
}

resource "aws_iam_user_policy_attachment" "ghactions-app-GH-Upload-To-S3-policy-attachment" {
  user       = var.iam_ghactions_app_user
  policy_arn = aws_iam_policy.GH-Upload-To-S3.arn
}