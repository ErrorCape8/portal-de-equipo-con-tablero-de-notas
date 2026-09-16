#!/bin/bash
set -e

STACK_NAME="portal-equipo-tablero-notas"
REGION="us-east-1"

echo "Vaciando el bucket de frontend antes de borrar el stack..."
BUCKET_NAME=$(aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='FrontendBucketName'].OutputValue" \
    --output text)

if [ -n "$BUCKET_NAME" ]; then
    aws s3 rm "s3://$BUCKET_NAME" --recursive --region "$REGION"
fi

echo "Borrando el stack completo..."
aws cloudformation delete-stack --stack-name "$STACK_NAME" --region "$REGION"

echo "Esperando a que termine de borrarse (puede tardar varios minutos por CloudFront)..."
aws cloudformation wait stack-delete-complete --stack-name "$STACK_NAME" --region "$REGION"

echo "Stack eliminado."