package com.serhiishcherbakov.notebooks.domain.outbox;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class OutboxEvent {
    private final Long id;
    private final OutboxEventType type;
    private final boolean processed;
    private final Instant createdAt;
    private final int attempts;
    private final String data;

    public boolean isNew() {
        return id == null || id == 0;
    }
}
