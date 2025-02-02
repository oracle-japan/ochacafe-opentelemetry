#!/bin/bash

export JAVA_TOOL_OPTIONS="-javaagent:../libs/opentelemetry-javaagent.jar" \
    OTEL_SERVICE_NAME=rolldice-zero-code \
    OTEL_EXPORTER_OTLP_PROTOCOL=grpc \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317 \
    OTEL_EXPORTER_OTLP_INSECURE=true \
    OTEL_TRACES_EXPORTER=otlp \
    OTEL_METRICS_EXPORTER=otlp \
    OTEL_LOGS_EXPORTER=otlp

java \
    -Dserver.port=8081 \
    -jar ../rolldice-zero-code/build/libs/rolldice-zero-code-1.0.0.jar
