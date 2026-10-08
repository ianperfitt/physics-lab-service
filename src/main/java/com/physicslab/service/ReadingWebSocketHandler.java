package com.physicslab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ReadingWebSocketHandler extends TextWebSocketHandler {

  private final ServerSentEventsService readings;
  private final ObjectMapper objectMapper;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
  private final ConcurrentHashMap<String, ScheduledFuture<?>> streams = new ConcurrentHashMap<>();

  public ReadingWebSocketHandler(ServerSentEventsService readings, ObjectMapper objectMapper) {
    this.readings = readings;
    this.objectMapper = objectMapper;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    ScheduledFuture<?> stream = scheduler.scheduleAtFixedRate(() -> sendReading(session), 0, 1000, TimeUnit.MILLISECONDS);
    streams.put(session.getId(), stream);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    synchronized (session) {
      ScheduledFuture<?> stream = streams.remove(session.getId());
      if (stream != null) stream.cancel(false);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    synchronized (session) {
      com.fasterxml.jackson.databind.JsonNode command;
      try {
        command = objectMapper.readTree(message.getPayload());
      } catch (IOException exception) {
        sendJson(session, Map.of("type", "error", "message", "Send a valid JSON command."));
        return;
      }
      if (command == null || !"setInterval".equals(command.path("type").asText())
          || !command.path("intervalMs").isIntegralNumber()
          || !command.path("intervalMs").canConvertToInt()
          || command.path("intervalMs").asInt() < 500
          || command.path("intervalMs").asInt() > 5000) {
        sendJson(session, Map.of("type", "error", "message", "Use setInterval with intervalMs between 500 and 5000."));
        return;
      }
      int intervalMs = command.path("intervalMs").asInt();
      ScheduledFuture<?> previous = streams.remove(session.getId());
      if (previous != null) previous.cancel(false);
      sendJson(session, Map.of("type", "intervalChanged", "intervalMs", intervalMs));
      streams.put(session.getId(), scheduler.scheduleAtFixedRate(
          () -> sendReading(session), intervalMs, intervalMs, TimeUnit.MILLISECONDS));
    }
  }

  private void sendJson(WebSocketSession session, Object message) throws IOException {
    synchronized (session) {
      if (session.isOpen()) {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
      }
    }
  }

  private void sendReading(WebSocketSession session) {
    if (!session.isOpen()) {
      return;
    }

    try {
      sendJson(session, readings.currentReading());
    } catch (IOException exception) {
      try {
        session.close(CloseStatus.SERVER_ERROR);
      } catch (IOException ignored) {
        // The connection may already be closed.
      }
    }
  }

  @PreDestroy
  public void shutdownScheduler() {
    scheduler.shutdownNow();
  }
}