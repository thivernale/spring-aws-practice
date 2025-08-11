#!/bin/bash

set -e

aws s3 mb s3://test-bucket --profile localstack
echo "========= List of buckets ============"
aws s3 ls --profile localstack

aws sqs create-queue --queue-name testQueue --profile localstack
echo "========= List of SQS Queues ============"
aws sqs list-queues --profile localstack

aws sns create-topic --name testTopic --profile localstack
echo "========= List of SNS Topics ============"
aws sns list-topics --profile localstack

aws secretsmanager create-secret --name /spring/secret --secret-string '{"app.apiKey": "secret123455", "anotherSecret": "shhhh"}' --region eu-central-1 --profile localstack
aws secretsmanager create-secret --name /secrets/api-secrets --secret-string '{"app.apiKey": "secret123456", "anotherSecret": "api-secrets"}' --region eu-central-1 --profile localstack
echo "========= List of secrets ============"
aws secretsmanager list-secrets --profile localstack

# Create DynamoDB table
aws dynamodb create-table \
    --table-name order \
    --attribute-definitions AttributeName=orderId,AttributeType=S \
    --key-schema AttributeName=orderId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --profile localstack
aws dynamodb list-tables --profile localstack

echo "LocalStack initialized successfully"
