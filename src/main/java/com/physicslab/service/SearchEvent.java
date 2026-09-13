package com.physicslab.service;

import java.time.Instant;

public record SearchEvent(
    String type,
    String source,
    String payload,
    Instant createdAt) {
}
