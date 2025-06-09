package com.serhiishcherbakov.notebooks.messaging;

import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import com.serhiishcherbakov.notebooks.messaging.model.BaseMessage;
import com.serhiishcherbakov.notebooks.messaging.model.EventType;
import com.serhiishcherbakov.notebooks.messaging.model.NotebookMessage;
import com.serhiishcherbakov.notebooks.messaging.model.TagMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class MessageBodyConverter {

    public BaseMessage<NotebookMessage> createNotebookMessage(EventType eventType, Notebook[] notebooks) {
        return new BaseMessage<>(eventType, Instant.now(), Arrays.stream(notebooks).map(this::mapNotebook).toList());
    }

    public BaseMessage<TagMessage> createTagMessage(EventType eventType, Tag[] tags) {
        return new BaseMessage<>(eventType, Instant.now(), Arrays.stream(tags).map(this::mapTag).toList());
    }

    private NotebookMessage mapNotebook(Notebook notebook) {
        return NotebookMessage.builder()
                .id(notebook.getId())
                .title(notebook.getTitle())
                .body(notebook.getBody())
                .tags(mapTags(notebook.getTags()))
                .userId(notebook.getUserId())
                .createdAt(notebook.getCreatedAt())
                .updatedAt(notebook.getUpdatedAt())
                .deletedAt(notebook.getDeletedAt())
                .build();
    }

    private List<TagMessage> mapTags(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(this::mapTag).toList();
    }

    private TagMessage mapTag(Tag tag) {
        return TagMessage.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .color(tag.getColor())
                .userId(tag.getUserId())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
