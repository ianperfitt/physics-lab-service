package com.juniortosenior.service;

import java.time.Instant;
import java.util.List;

public record RenderingData(
    String message,
    Instant generatedAt,
    List<Item> items) {
  public record Item(int id, String name) {
  }
}
