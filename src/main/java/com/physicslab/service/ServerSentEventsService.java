package com.physicslab.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ServerSentEventsService {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

  public SseEmitter stream() {
    SseEmitter emitter = new SseEmitter(0L);
    AtomicLong sequence = new AtomicLong();

    ScheduledFuture<?> streamTask = scheduler.scheduleAtFixedRate(() -> {
      long currentSequence = sequence.incrementAndGet();
      double temperatureC = Math.round((21.5 + 2 * Math.sin(currentSequence / 3.0)) * 10.0) / 10.0;
      Map<String, Object> reading = Map.of(
          "sequence", currentSequence,
          "timestamp", Instant.now().toString(),
          "temperatureC", temperatureC);

      try {
        emitter.send(SseEmitter.event()
            .name("reading")
            .id(Long.toString(currentSequence))
            .data(reading));
      } catch (IOException exception) {
        emitter.completeWithError(exception);
      }
    }, 0, 1, TimeUnit.SECONDS);

    emitter.onCompletion(() -> streamTask.cancel(true));
    emitter.onTimeout(() -> {
      streamTask.cancel(true);
      emitter.complete();
    });
    emitter.onError(exception -> streamTask.cancel(true));

    return emitter;
  }

  @PreDestroy
  public void shutdownScheduler() {
    scheduler.shutdownNow();
  }
}