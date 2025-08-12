#!/bin/bash

set -e

export AWS_DEFAULT_REGION=eu-central-1

awslocal s3 mb s3://test-bucket
echo "========= List of buckets ============"
awslocal s3 ls

awslocal sqs create-queue --queue-name testQueue
echo "========= List of SQS Queues ============"
awslocal sqs list-queues

awslocal sns create-topic --name testTopic
echo "========= List of SNS Topics ============"
awslocal sns list-topics

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

echo "LocalStack initialized successfully"
