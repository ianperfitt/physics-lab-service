package com.physicslab.service;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = { "http://localhost:3000" })
public class ServerSentEventsController {

  private final ServerSentEventsService serverSentEventsService;

  public ServerSentEventsController(ServerSentEventsService serverSentEventsService) {
    this.serverSentEventsService = serverSentEventsService;
  }

  @GetMapping(value = "/stream", produces = "text/event-stream")
  public SseEmitter stream() {
    return serverSentEventsService.stream();
  }
}