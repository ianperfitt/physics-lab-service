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
public class RowEchelonController {

  private final RowEchelonService rowEchelonService;

  public RowEchelonController(RowEchelonService rowEchelonService) {
    this.rowEchelonService = rowEchelonService;
  }

  @PostMapping("/row-echelon")
  public RowEchelonResponse rowEchelon(@RequestBody RowEchelonRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("request body is required");
    }
    return rowEchelonService.reduce(request.matrix());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleInvalidMatrix(IllegalArgumentException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", exception.getMessage()));
  }
}
