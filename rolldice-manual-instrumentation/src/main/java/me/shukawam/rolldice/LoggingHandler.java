package me.shukawam.rolldice;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import io.opentelemetry.api.logs.Logger;

/**
 * JUL(Java Util Logging)をOpenTelemetryのログに変換するためのハンドラークラス
 * 
 * @author shukawam
 */
public class LoggingHandler extends Handler {
    private final Logger otelLogger;

    public LoggingHandler() {
        var openTelemetry = new OpenTelemetryConfiguration().openTelemetry();
        this.otelLogger = openTelemetry.getLogsBridge().get("rolldice-manual-inst");
    }

    @Override
    public void publish(LogRecord record) {
        otelLogger.logRecordBuilder()
                .setSeverityText(record.getLevel().getName())
                .setBody(record.getMessage())
                .emit();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
