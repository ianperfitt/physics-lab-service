package com.physicslab.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SearchEventPublisher {

  private final ObjectProvider<KafkaTemplate<String, SearchEvent>> kafkaTemplateProvider;

  public SearchEventPublisher(ObjectProvider<KafkaTemplate<String, SearchEvent>> kafkaTemplateProvider) {
    this.kafkaTemplateProvider = kafkaTemplateProvider;
  }

  public void publish(SearchEvent event) {
    KafkaTemplate<String, SearchEvent> kafkaTemplate = kafkaTemplateProvider.getIfAvailable();
    if (kafkaTemplate != null) {
      kafkaTemplate.send("physicslab.search.events", event.type(), event);
    }
  }
}
