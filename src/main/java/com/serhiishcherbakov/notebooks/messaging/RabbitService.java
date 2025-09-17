package com.serhiishcherbakov.notebooks.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serhiishcherbakov.notebooks.config.RabbitProperties;
import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;
import com.serhiishcherbakov.notebooks.domain.outbox.OutboxEvent;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
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
    private final ObjectMapper objectMapper;

    public void publishEvent(OutboxEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(
                rabbitProperties.exchanges().notebooks(),
                getRoutingKeyForEvent(event),
                convertToMessage(event, getDataClassForEvent(event)));
    }

    private String getRoutingKeyForEvent(OutboxEvent event) {
        return switch (event.getType()) {
            case NOTEBOOK_CREATED -> rabbitProperties.routingKeys().notebooksCreated();
            case NOTEBOOK_UPDATED -> rabbitProperties.routingKeys().notebooksUpdated();
            case NOTEBOOK_DELETED -> rabbitProperties.routingKeys().notebooksDeleted();
            case TAG_CREATED -> rabbitProperties.routingKeys().tagsCreated();
            case TAG_UPDATED -> rabbitProperties.routingKeys().tagsUpdated();
            case TAG_DELETED -> rabbitProperties.routingKeys().tagsDeleted();
        };
    }

    private Class<?> getDataClassForEvent(OutboxEvent event) {
        return switch (event.getType()) {
            case NOTEBOOK_CREATED, NOTEBOOK_UPDATED, NOTEBOOK_DELETED -> Notebook.class;
            case TAG_CREATED, TAG_UPDATED, TAG_DELETED -> Tag.class;
        };
    }

    private <T> Message<T> convertToMessage(OutboxEvent event, Class<T> dataClass) throws JsonProcessingException {
        return Message.<T>builder()
                .id(event.getId())
                .type(event.getType())
                .createdAt(event.getCreatedAt())
                .data(objectMapper.readValue(event.getData(), dataClass))
                .build();
    }
}
