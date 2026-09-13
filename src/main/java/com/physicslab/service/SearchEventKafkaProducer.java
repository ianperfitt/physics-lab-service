package com.physicslab.service;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class SearchEventKafkaProducer {

  private final KafkaTemplate<String, SearchEvent> kafkaTemplate;

  public SearchEventKafkaProducer(KafkaTemplate<String, SearchEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(String topic, SearchEvent event) {
    kafkaTemplate.send(topic, event.type(), event);
  }
}
