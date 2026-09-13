package com.physicslab.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "physicslab-index")
public class SearchDocument {

  @Id
  private String id;

  private String type;
  private String title;

  @Field(type = FieldType.Text)
  private String body;

  @Field(type = FieldType.Keyword)
  private List<String> tags;

  private Instant createdAt;

  public SearchDocument() {
  }

  public SearchDocument(String id, String type, String title, String body, List<String> tags, Instant createdAt) {
    this.id = id;
    this.type = type;
    this.title = title;
    this.body = body;
    this.tags = tags;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
