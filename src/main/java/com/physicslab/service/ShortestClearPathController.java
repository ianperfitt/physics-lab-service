package com.physicslab.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matrices")
@CrossOrigin(origins = { "http://localhost:3000" })
public class ShortestClearPathController {

  private final ShortestClearPathService shortestClearPathService;

  public ShortestClearPathController(ShortestClearPathService shortestClearPathService) {
    this.shortestClearPathService = shortestClearPathService;
  }

  @PostMapping("/shortest-clear-path")
  public ShortestClearPathResponse shortestClearPath(@RequestBody ShortestClearPathRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("request body is required");
    }

    return shortestClearPathService.shortestPath(request.grid());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleInvalidMatrix(IllegalArgumentException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", exception.getMessage()));
  }
}
