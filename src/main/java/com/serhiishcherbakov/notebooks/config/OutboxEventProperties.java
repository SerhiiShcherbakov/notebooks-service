package com.serhiishcherbakov.notebooks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxEventProperties(int maxRetryAttempts,
                                    int batchSize) {
}
