package com.serhiishcherbakov.notebooks.messaging.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class BaseMessage<T> {
    private final EventType type;
    private final Instant timestamp;
    private final List<T> payload;
}
