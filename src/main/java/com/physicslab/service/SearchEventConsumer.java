package com.physicslab.service;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class SearchEventConsumer {

  @KafkaListener(topics = "physicslab.search.events", groupId = "physicslab-index")
  public void handle(SearchEvent event) {
    // This is the consumer endpoint for the searchable event stream.
  }
}
