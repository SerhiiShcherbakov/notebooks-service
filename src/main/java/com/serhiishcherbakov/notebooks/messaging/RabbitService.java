package com.serhiishcherbakov.notebooks.messaging;

import com.serhiishcherbakov.notebooks.config.RabbitProperties;
import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import com.serhiishcherbakov.notebooks.messaging.model.BaseMessage;
import com.serhiishcherbakov.notebooks.messaging.model.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;
    private final MessageBodyConverter messageBodyConverter;

    public void publishNotebookCreatedEvent(Notebook... notebooks) {
        convertAndSend(rabbitProperties.routingKeys().notebooksCreated(),
                messageBodyConverter.createNotebookMessage(EventType.NOTEBOOK_CREATED, notebooks));
    }

    public void publishNotebookUpdatedEvent(Notebook... notebooks) {
        convertAndSend(rabbitProperties.routingKeys().notebooksUpdated(),
                messageBodyConverter.createNotebookMessage(EventType.NOTEBOOK_UPDATED, notebooks));
    }

    public void publishNotebookDeletedEvent(Notebook... notebooks) {
        convertAndSend(rabbitProperties.routingKeys().notebooksDeleted(),
                messageBodyConverter.createNotebookMessage(EventType.NOTEBOOK_DELETED, notebooks));
    }

    public void publishTagCreatedEvent(Tag... tags) {
        convertAndSend(rabbitProperties.routingKeys().tagsCreated(),
                messageBodyConverter.createTagMessage(EventType.TAG_CREATED, tags));
    }

    public void publishTagUpdatedEvent(Tag... tags) {
        convertAndSend(rabbitProperties.routingKeys().tagsUpdated(),
                messageBodyConverter.createTagMessage(EventType.TAG_UPDATED, tags));
    }

    public void publishTagDeletedEvent(Tag... tags) {
        convertAndSend(rabbitProperties.routingKeys().tagsDeleted(),
                messageBodyConverter.createTagMessage(EventType.TAG_DELETED, tags));
    }


    private void convertAndSend(String routingKey, BaseMessage<?> message) {
        try {
            rabbitTemplate.convertAndSend(rabbitProperties.exchanges().notebooks(), routingKey, message);
        } catch (Exception e) {
            log.error("Failed to send message to {} - {}", routingKey, e.getMessage());
        }
    }
}
