package com.physicslab.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenderingService {

  private final RenderingRepository renderingRepository;

  @Autowired
  public RenderingService(RenderingRepository renderingRepository) {
    this.renderingRepository = renderingRepository;
  }

  public RenderingData getSsrData() {
    return getData("ssr");
  }

  public RenderingData getSsgData() {
    return getData("ssg");
  }

  public RenderingData getIsrData() {
    return getData("isr");
  }

  public RenderingData getCsrData() {
    return getData("csr");
  }

  private RenderingData getData(String mode) {
    RenderingRepository.RenderingTemplate template = renderingRepository.findByMode(mode)
        .orElseThrow(() -> new IllegalArgumentException("Unknown rendering mode: " + mode));
    List<RenderingData.Item> items = IntStream.range(0, template.itemNames().size())
        .mapToObj(index -> new RenderingData.Item(index + 1, template.itemNames().get(index)))
        .toList();
    return new RenderingData(template.message(), Instant.now(), items);
  }
}
