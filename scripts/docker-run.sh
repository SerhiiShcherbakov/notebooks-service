#!/bin/bash

docker run -ti --rm -p 8080:8080 \
  --network="host" \
  -e JAVA_TOOL_OPTIONS="-Dspring.profiles.active=local" \
  -e GATEWAY_KEY="test" \
  -e DB_URL="jdbc:mysql://localhost:3306/notebooks_db" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="verysecret" \
  -e RABBITMQ_ADDRESSES="localhost:5672" \
  -e RABBITMQ_USERNAME="myuser" \
  -e RABBITMQ_PASSWORD="secret" \
  --name="notebooks-service" "notebooks-service:1.0.0"