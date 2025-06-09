package com.serhiishcherbakov.notebooks.domain.notebook;

import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Notebook {
    private final Long id;
    private final String title;
    private final String body;
    private final List<Tag> tags;
    private final String userId;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;

    public boolean isNew() {
        return id == null || id == 0;
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
