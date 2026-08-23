package com.juniortosenior.service;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rendering")
@CrossOrigin(origins = { "http://localhost:3000" })
public class RenderingController {

  @GetMapping("/ssr")
  public RenderingData ssr() {
    return data("SSR demo data", "Delta", "Echo", "Foxtrot");
  }

  @GetMapping("/ssg")
  public RenderingData ssg() {
    return data("SSG demo data", "Golf", "Hotel", "India");
  }

  @GetMapping("/isr")
  public RenderingData isr() {
    return data("ISR demo data", "Alpha", "Bravo", "Charlie");
  }

  @GetMapping("/csr")
  public RenderingData csr() {
    return data("CSR demo data", "Juliet", "Kilo", "Lima");
  }

  private RenderingData data(String message, String... itemNames) {
    List<RenderingData.Item> items = java.util.stream.IntStream.range(0, itemNames.length)
        .mapToObj(index -> new RenderingData.Item(index + 1, itemNames[index]))
        .toList();
    return new RenderingData(message, Instant.now(), items);
  }
}
