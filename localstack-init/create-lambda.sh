#!/bin/bash
set -e

cd /lambda-src/dashboard-metrics
zip -r /tmp/dashboard-metrics.zip index.js

awslocal lambda create-function \
    --function-name dashboard-metrics \
    --runtime nodejs18.x \
    --handler index.handler \
    --zip-file fileb:///tmp/dashboard-metrics.zip \
    --role arn:aws:iam::000000000000:role/lambda-role

echo "Lambda dashboard-metrics registrada en LocalStack"