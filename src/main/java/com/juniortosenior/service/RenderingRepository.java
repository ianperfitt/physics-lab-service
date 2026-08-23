package com.juniortosenior.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class RenderingRepository {

  private final Map<String, RenderingTemplate> templates = Map.of(
      "ssr", new RenderingTemplate("SSR demo data", List.of("Delta", "Echo", "Foxtrot")),
      "ssg", new RenderingTemplate("SSG demo data", List.of("Golf", "Hotel", "India")),
      "isr", new RenderingTemplate("ISR demo data", List.of("Alpha", "Bravo", "Charlie")),
      "csr", new RenderingTemplate("CSR demo data", List.of("Juliet", "Kilo", "Lima")));

  public Optional<RenderingTemplate> findByMode(String mode) {
    return Optional.ofNullable(templates.get(mode));
  }

  public record RenderingTemplate(String message, List<String> itemNames) {
  }
}
