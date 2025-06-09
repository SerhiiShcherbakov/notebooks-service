package com.serhiishcherbakov.notebooks.messaging.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class NotebookMessage {
    private final Long id;
    private final String title;
    private final String body;
    private final List<TagMessage> tags;
    private final String userId;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;
}
