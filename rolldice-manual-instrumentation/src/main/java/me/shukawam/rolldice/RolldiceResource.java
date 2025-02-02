package me.shukawam.rolldice;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/rolldice")
@RequestScoped
public class RolldiceResource {
    private static final Logger logger = Logger.getLogger(RolldiceResource.class.getName());

    private final Tracer tracer;
    private final LongCounter dicerollCounter;

    @Inject
    public RolldiceResource(Tracer tracer, Meter meter) {
        this.tracer = tracer;
        this.dicerollCounter = meter.counterBuilder("dice.roll")
                .setDescription("Number of times the dice was rolled")
                .setUnit("1")
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index(@QueryParam("player") Optional<String> player) {
        Span span = tracer.spanBuilder("get_index").startSpan();
        try {
            int result = this.getRandomNumber(1, 6, span);
            dicerollCounter.add(1);
            if (player.isPresent()) {
                logger.info(String.format("%s is rolling the dice: %s", player.get(), result));
            } else {
                logger.info(String.format("Anonymous player is rolling the dice: %s", result));
            }
            return Integer.toString(result);
        } finally {
            span.end();
        }
    }

    private int getRandomNumber(int min, int max, Span parentSpan) {
        Context parentContext = parentSpan.storeInContext(Context.current());
        Span span = tracer.spanBuilder("get_ramdom_number").setParent(parentContext).startSpan();
        try {
            sleep(min, max, span);
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        } catch (Exception e) {
            span.recordException(e);
            logger.severe(e.getMessage());
            throw new RuntimeException();
        } finally {
            span.end();
        }
    }

    private void sleep(int min, int max, Span parentSpan) {
        Context parentContext = parentSpan.storeInContext(Context.current());
        Span span = tracer.spanBuilder("sleep").setParent(parentContext).startSpan();
        try {
            var ramdom = ThreadLocalRandom.current().nextInt(min, max + 1);
            Thread.sleep(ramdom * 1000);
        } catch (Exception e) {
            span.recordException(e);
            logger.severe(e.getMessage());
            throw new RuntimeException();
        } finally {
            span.end();
        }
    }
}
