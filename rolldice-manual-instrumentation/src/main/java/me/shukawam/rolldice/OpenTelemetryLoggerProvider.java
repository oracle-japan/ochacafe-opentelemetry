package me.shukawam.rolldice;

import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class OpenTelemetryLoggerProvider {

    private final SdkLoggerProvider loggerProvider;

    public OpenTelemetryLoggerProvider(SdkLoggerProvider loggerProvider) {
        this.loggerProvider = loggerProvider;
    }

    @Produces
    public LoggerProvider loggerProvider() {
        return loggerProvider;
    }

}
