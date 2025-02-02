package me.shukawam.rolldice;

import java.util.concurrent.TimeUnit;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class OpenTelemetryConfiguration {

	private static final String OTLP_GRPC_ENDPOINT = "http://localhost:4317";

	@Produces
	public OpenTelemetry openTelemetry() {
		Resource resource = Resource.getDefault()
				.merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "rolldice-manual-inst")));
		// トレース
		OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
				.setEndpoint(OTLP_GRPC_ENDPOINT)
				.build();
		SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
				.setResource(resource)
				.addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
				.build();
		// ログ
		OtlpGrpcLogRecordExporter logExporter = OtlpGrpcLogRecordExporter.builder()
				.setEndpoint(OTLP_GRPC_ENDPOINT)
				.build();
		SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
				.setResource(resource)
				.addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
				.build();
		// メトリクス
		OtlpGrpcMetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
				.setEndpoint(OTLP_GRPC_ENDPOINT)
				.build();
		SdkMeterProvider meterProvider = SdkMeterProvider.builder()
				.setResource(resource)
				.registerMetricReader(PeriodicMetricReader.builder(metricExporter)
						.setInterval(1, TimeUnit.MINUTES).build())
				.build();
		return OpenTelemetrySdk.builder()
				.setTracerProvider(tracerProvider)
				.setLoggerProvider(loggerProvider)
				.setMeterProvider(meterProvider)
				.build();
	}

	@Produces
	public Tracer tracer(OpenTelemetry openTelemetry) {
		return openTelemetry.getTracer("rolldice-manual-inst", "1.0.0");
	}

	@Produces
	public Meter meter(OpenTelemetry openTelemetry) {
		return openTelemetry.getMeter("me.shukawam.rolldice");
	}
}
