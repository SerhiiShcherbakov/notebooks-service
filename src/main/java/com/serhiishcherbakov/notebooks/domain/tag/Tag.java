package com.serhiishcherbakov.notebooks.domain.tag;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class Tag {
    private final Long id;
    private final String title;
    private final String color;
    private final String userId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public boolean isNew() {
        return id == null || id == 0;
    }


}