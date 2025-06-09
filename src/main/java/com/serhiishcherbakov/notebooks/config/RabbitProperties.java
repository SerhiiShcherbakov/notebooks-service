package com.serhiishcherbakov.notebooks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitProperties(Exchanges exchanges,
                               RoutingKeys routingKeys) {

    public record Exchanges(String notebooks) {}

    public record RoutingKeys(String notebooksCreated,
                              String notebooksUpdated,
                              String notebooksDeleted,
                              String tagsCreated,
                              String tagsUpdated,
                              String tagsDeleted) {}
}
