package com.serhiishcherbakov.notebooks.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serhiishcherbakov.notebooks.config.OutboxEventProperties;
import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import com.serhiishcherbakov.notebooks.messaging.RabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventConverter outboxEventConverter;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventProperties outboxEventProperties;
    private final RabbitService rabbitService;

    @Transactional
    public void saveNotebookOutboxEvent(Notebook notebook, OutboxEventType eventType) {
        saveOutboxEvents(outboxEventConverter.convertNotebooksToOutboxEvents(List.of(notebook), eventType));
    }

    @Transactional
    public void saveNotebookOutboxEvent(List<Notebook> notebook, OutboxEventType eventType) {
        saveOutboxEvents(outboxEventConverter.convertNotebooksToOutboxEvents(notebook, eventType));
    }

    @Transactional
    public void saveTagOutboxEvent(Tag tag, OutboxEventType eventType) {
        saveOutboxEvents(outboxEventConverter.convertTagsToOutboxEvents(List.of(tag), eventType));
    }

    private void saveOutboxEvents(List<OutboxEvent> events) {
        outboxEventRepository.addAll(events);
    }

    public void sendOutboxEvents() {
        var events = outboxEventRepository.findUnprocessedEventsToPublish(outboxEventProperties.maxRetryAttempts(), outboxEventProperties.batchSize());

        log.info("Sending {} outbox events", events.size());

        if (events.isEmpty()) {
            return;
        }

        for (var event : events) {
            try {
                rabbitService.publishEvent(event);
                outboxEventRepository.update(event.toBuilder().processed(true).build());
                log.info("Event {} sent", event.getId());
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize data for event: {}", event.getId());
                incrementAttemptsAndUpdateEvent(event);
            } catch (RuntimeException e) {
                log.error("Failed sending event {}: {}", event.getId(), e.getMessage());
                incrementAttemptsAndUpdateEvent(event);
            }
        }
    }

    private void incrementAttemptsAndUpdateEvent(OutboxEvent event) {
        outboxEventRepository.update(event.toBuilder().attempts(event.getAttempts() + 1).build());
    }
}
