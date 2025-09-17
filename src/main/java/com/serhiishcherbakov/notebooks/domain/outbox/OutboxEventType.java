package com.serhiishcherbakov.notebooks.domain.outbox;

public enum OutboxEventType {
    NOTEBOOK_CREATED,
    NOTEBOOK_UPDATED,
    NOTEBOOK_DELETED,
    TAG_CREATED,
    TAG_UPDATED,
    TAG_DELETED
}
