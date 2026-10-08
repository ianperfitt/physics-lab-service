package com.physicslab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

class ReadingWebSocketHandlerTest {
  @Test
  void acknowledgesValidCommandAndStreamsAtRequestedInterval() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ServerSentEventsService readings = mock(ServerSentEventsService.class);
    when(readings.currentReading()).thenReturn(java.util.Map.of("sequence", 1));
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.isOpen()).thenReturn(true);
    when(session.getId()).thenReturn("test");
    ReadingWebSocketHandler handler = new ReadingWebSocketHandler(readings, mapper);
    try {
      handler.handleTextMessage(session, new TextMessage("{\"type\":\"setInterval\",\"intervalMs\":500}"));
      ArgumentCaptor<TextMessage> messages = ArgumentCaptor.forClass(TextMessage.class);
      verify(session, timeout(1500).times(2)).sendMessage(messages.capture());
      assertEquals("intervalChanged", mapper.readTree(messages.getAllValues().get(0).getPayload()).path("type").asText());
      assertEquals(500, mapper.readTree(messages.getAllValues().get(0).getPayload()).path("intervalMs").asInt());
      assertEquals(1, mapper.readTree(messages.getAllValues().get(1).getPayload()).path("sequence").asInt());
    } finally {
      handler.shutdownScheduler();
    }
  }

  @Test
  void rejectsMalformedAndOutOfRangeCommands() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.isOpen()).thenReturn(true);
    ReadingWebSocketHandler handler = new ReadingWebSocketHandler(mock(ServerSentEventsService.class), mapper);
    try {
      handler.handleTextMessage(session, new TextMessage("invalid json"));
      handler.handleTextMessage(session, new TextMessage("{\"type\":\"setInterval\",\"intervalMs\":10}"));
      ArgumentCaptor<TextMessage> messages = ArgumentCaptor.forClass(TextMessage.class);
      verify(session, times(2)).sendMessage(messages.capture());
      for (TextMessage message : messages.getAllValues()) {
        assertEquals("error", mapper.readTree(message.getPayload()).path("type").asText());
      }
    } finally {
      handler.shutdownScheduler();
    }
  }
}
