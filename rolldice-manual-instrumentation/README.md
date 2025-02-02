# rolldice-manual-instrumentation

## Build and run

With JDK21

build

```bash
mvn package
```

run

```bash
export OTEL_SERVICE_NAME="rolldice-manual-inst"
export OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318"
java -jar target/rolldice-manual-instrumentation.jar
```
