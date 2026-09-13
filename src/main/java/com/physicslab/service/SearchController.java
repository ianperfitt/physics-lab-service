package com.physicslab.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = { "http://localhost:3000" })
public class SearchController {

  private final SearchIndexService searchIndexService;

  @Autowired
  public SearchController(SearchIndexService searchIndexService) {
    this.searchIndexService = searchIndexService;
  }

  @PostMapping("/documents")
  public SearchDocument indexDocument(@RequestBody SearchDocument document) {
    return searchIndexService.indexDocument(document);
  }

  @GetMapping("/documents")
  public List<SearchDocument> documents() {
    return searchIndexService.getDocuments();
  }

  @GetMapping("/search")
  public List<SearchDocument> search(@RequestParam(name = "q", defaultValue = "") String query) {
    return searchIndexService.search(query);
  }

  @GetMapping("/events")
  public List<SearchEvent> events() {
    return searchIndexService.getEvents();
  }
}
