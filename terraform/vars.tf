variable "bucket-name" {
  default = "my-bucket"
}
variable "sns-topic-names" {
  type    = list(string)
  default = ["file-upload-topic", "file-delete-topic"]
}

variable "service_arn" {
  type    = string
  default = "service-arn"
}