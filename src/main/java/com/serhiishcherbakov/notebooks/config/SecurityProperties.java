package com.serhiishcherbakov.notebooks.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private final List<ClientProperties> clients;

    @Data
    public static class ClientProperties {
        private final String name;
        private final String key;
    }
}
