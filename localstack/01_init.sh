#!/bin/bash

set -e

export AWS_DEFAULT_REGION=eu-central-1

awslocal s3 mb s3://test-bucket
echo "========= List of buckets ============"
awslocal s3 ls

awslocal sqs create-queue --queue-name testQueue
echo "========= List of SQS Queues ============"
awslocal sqs list-queues

#awslocal sns create-topic --name testTopic
#echo "========= List of SNS Topics ============"
#awslocal sns list-topics

awslocal secretsmanager create-secret --name /spring/secret --secret-string '{"app.apiKey": "secret123455", "anotherSecret": "shhhh"}'
awslocal secretsmanager create-secret --name /secrets/api-secrets --secret-string '{"app.apiKey": "secret123456", "anotherSecret": "api-secrets"}'
echo "========= List of secrets ============"
awslocal secretsmanager list-secrets

# Create DynamoDB table
awslocal dynamodb create-table \
    --table-name order \
    --attribute-definitions AttributeName=orderId,AttributeType=S \
    --key-schema AttributeName=orderId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
awslocal dynamodb list-tables



# Names
TOPIC_NAME="testTopic"
QUEUE_NAME="topicQueue"

# Create SNS topic
TOPIC_ARN=$(awslocal sns create-topic --name "$TOPIC_NAME" --query 'TopicArn' --output text)
echo "Created SNS topic: $TOPIC_ARN"

# Create SQS queue
QUEUE_URL=$(awslocal sqs create-queue --queue-name "$QUEUE_NAME" --query 'QueueUrl' --output text)
echo "Created SQS queue: $QUEUE_URL"

# Get SQS Queue ARN
QUEUE_ARN=$(awslocal sqs get-queue-attributes --queue-url "$QUEUE_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

# Allow SNS to send messages to SQS (set proper policy)
POLICY=$(cat <<EOF
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "Allow-SNS-SendMessage",
    "Effect": "Allow",
    "Principal": "*",
    "Action": "SQS:SendMessage",
    "Resource": "$QUEUE_ARN",
    "Condition": {
      "ArnEquals": {
        "aws:SourceArn": "$TOPIC_ARN"
      }
    }
  }]
}
EOF
)

awslocal sqs set-queue-attributes \
  --queue-url "$QUEUE_URL" \
  --attributes Policy="$(echo "$POLICY" | jq -c .)"

echo "Set SQS queue policy to allow SNS publishing."

# Subscribe SQS queue to SNS topic
awslocal sns subscribe \
  --topic-arn "$TOPIC_ARN" \
  --protocol sqs \
  --notification-endpoint "$QUEUE_ARN"

echo "Subscribed SQS queue to SNS topic."

echo "LocalStack initialized successfully"
