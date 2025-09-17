package com.serhiishcherbakov.notebooks.messaging;

import com.serhiishcherbakov.notebooks.domain.outbox.OutboxEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Message<T> {
    private final Long id;
    private final OutboxEventType type;
    private final Instant createdAt;
    private final T data;
}
