package com.serhiishcherbakov.notebooks.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import com.serhiishcherbakov.notebooks.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.serhiishcherbakov.notebooks.exception.Error.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventConverter {
    private final ObjectMapper objectMapper;

    public List<OutboxEvent> convertNotebooksToOutboxEvents(List<Notebook> notebooks, OutboxEventType eventType) {
        return convertToOutboxEvents(notebooks, eventType);
    }

    public List<OutboxEvent> convertTagsToOutboxEvents(List<Tag> tags, OutboxEventType eventType) {
        return convertToOutboxEvents(tags, eventType);
    }

    private <T> List<OutboxEvent> convertToOutboxEvents(List<T> items, OutboxEventType eventType) {
        return items.stream()
                .map(item -> convertToOutboxEvent(item, eventType))
                .toList();
    }

    private <T> OutboxEvent convertToOutboxEvent(T item, OutboxEventType eventType) {
        try {
            return OutboxEvent.builder()
                    .type(eventType)
                    .processed(false)
                    .data(objectMapper.writeValueAsString(item))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Failed to convert notebook to JSON: {}", e.getMessage());
            throw new AppException(INTERNAL_SERVER_ERROR);
        }
    }
}
