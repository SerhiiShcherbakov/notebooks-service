package com.serhiishcherbakov.notebooks.messaging.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class TagMessage {
    private final Long id;
    private final String title;
    private final String color;
    private final String userId;
    private final Instant createdAt;
    private final Instant updatedAt;
}
