package com.physicslab.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchIndexService {

  private final SearchDocumentRepository searchDocumentRepository;
  private final SearchEventPublisher searchEventPublisher;
  private final List<SearchEvent> events = new CopyOnWriteArrayList<>();
  private final List<SearchDocument> inMemoryDocuments = new CopyOnWriteArrayList<>();

  @Autowired
  public SearchIndexService(ObjectProvider<SearchDocumentRepository> searchDocumentRepositoryProvider,
      SearchEventPublisher searchEventPublisher) {
    this.searchDocumentRepository = searchDocumentRepositoryProvider.getIfAvailable();
    this.searchEventPublisher = searchEventPublisher;
    seedDemoDocuments();
  }

  public SearchDocument indexDocument(SearchDocument document) {
    if (document.getId() == null || document.getId().isBlank()) {
      document.setId(UUID.randomUUID().toString());
    }

    if (document.getCreatedAt() == null) {
      document.setCreatedAt(Instant.now());
    }

    SearchDocument saved = null;
    if (searchDocumentRepository != null) {
      saved = searchDocumentRepository.save(document);
    } else {
      saved = new SearchDocument(document.getId(), document.getType(), document.getTitle(),
          document.getBody(), document.getTags(), document.getCreatedAt());
      inMemoryDocuments.add(saved);
    }

    SearchEvent event = new SearchEvent(
        "physicslab.search.document.indexed",
        "rendering-service",
        "Indexed document: " + saved.getTitle(),
        Instant.now());

    events.add(event);
    searchEventPublisher.publish(event);

    return saved;
  }

  public List<SearchDocument> search(String query) {
    String normalizedQuery = query == null ? "" : query.trim().toLowerCase();

    if (normalizedQuery.isBlank()) {
      if (searchDocumentRepository != null) {
        return convertIterableToList(searchDocumentRepository.findAll());
      }
      return new ArrayList<>(inMemoryDocuments);
    }

    if (searchDocumentRepository != null) {
      List<SearchDocument> all = convertIterableToList(searchDocumentRepository.findAll());
      return all.stream()
          .filter(document -> containsMatch(document, normalizedQuery))
          .collect(Collectors.toList());
    }

    return inMemoryDocuments.stream()
        .filter(document -> containsMatch(document, normalizedQuery))
        .collect(Collectors.toList());
  }

  public List<SearchDocument> getDocuments() {
    if (searchDocumentRepository != null) {
      return convertIterableToList(searchDocumentRepository.findAll());
    }
    return new ArrayList<>(inMemoryDocuments);
  }

  public List<SearchEvent> getEvents() {
    return events;
  }

  private boolean containsMatch(SearchDocument document, String query) {
    boolean titleMatches = document.getTitle() != null
        && document.getTitle().toLowerCase().contains(query);
    boolean bodyMatches = document.getBody() != null
        && document.getBody().toLowerCase().contains(query);
    boolean tagMatches = document.getTags() != null
        && document.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query));

    return titleMatches || bodyMatches || tagMatches;
  }

  private List<SearchDocument> convertIterableToList(Iterable<SearchDocument> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }

  private void seedDemoDocuments() {
    if (searchDocumentRepository != null && searchDocumentRepository.count() == 0) {
      indexDocument(new SearchDocument(
          UUID.randomUUID().toString(),
          "concept",
          "Linear Algebra Foundations",
          "Vectors, matrices, and systems of equations introduce the structure of linear models.",
          List.of("linear-algebra", "vectors", "matrices"),
          Instant.now()));

      indexDocument(new SearchDocument(
          UUID.randomUUID().toString(),
          "problem",
          "Orbital Mechanics Escape Velocity",
          "A problem exploring escape velocity using Newtonian gravity and orbital energy.",
          List.of("orbital-mechanics", "escape-velocity", "gravity"),
          Instant.now()));
    }

    if (searchDocumentRepository == null && inMemoryDocuments.isEmpty()) {
      inMemoryDocuments.add(new SearchDocument(
          UUID.randomUUID().toString(),
          "concept",
          "Linear Algebra Foundations",
          "Vectors, matrices, and systems of equations introduce the structure of linear models.",
          List.of("linear-algebra", "vectors", "matrices"),
          Instant.now()));

      inMemoryDocuments.add(new SearchDocument(
          UUID.randomUUID().toString(),
          "problem",
          "Orbital Mechanics Escape Velocity",
          "A problem exploring escape velocity using Newtonian gravity and orbital energy.",
          List.of("orbital-mechanics", "escape-velocity", "gravity"),
          Instant.now()));
    }
  }
}
