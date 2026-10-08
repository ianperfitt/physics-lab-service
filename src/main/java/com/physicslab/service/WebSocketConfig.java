package com.physicslab.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final ReadingWebSocketHandler readingWebSocketHandler;

  public WebSocketConfig(ReadingWebSocketHandler readingWebSocketHandler) {
    this.readingWebSocketHandler = readingWebSocketHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(readingWebSocketHandler, "/api/events/socket")
        .setAllowedOrigins("http://localhost:3000");
  }
}