package com.serhiishcherbakov.notebooks.rest.dto.response;

import com.serhiishcherbakov.notebooks.domain.tag.Tag;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record TagDto(Long id,
                     String title,
                     String color,
                     String userId,
                     Instant createdAt,
                     Instant updatedAt) {

    public static TagDto of(Tag tag) {
        return new TagDto(
                tag.getId(),
                tag.getTitle(),
                tag.getColor(),
                tag.getUserId(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }

    public static List<TagDto> of(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(TagDto::of).toList();
    }
}
