resource "aws_sns_topic" "sns_topics" {
  count = length(var.sns-topic-names)
  name  = var.sns-topic-names[count.index]
}