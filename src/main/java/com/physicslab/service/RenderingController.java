package com.physicslab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rendering")
@CrossOrigin(origins = { "http://localhost:3000" })
public class RenderingController {

  @Autowired
  private RenderingService renderingService;

  @GetMapping("/ssr")
  public RenderingData ssr() {
    return renderingService.getSsrData();
  }

  @GetMapping("/ssg")
  public RenderingData ssg() {
    return renderingService.getSsgData();
  }

  @GetMapping("/isr")
  public RenderingData isr() {
    return renderingService.getIsrData();
  }

  @GetMapping("/csr")
  public RenderingData csr() {
    return renderingService.getCsrData();
  }
}
