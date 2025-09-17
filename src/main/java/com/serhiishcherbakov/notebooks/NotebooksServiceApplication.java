package com.serhiishcherbakov.notebooks;

import com.serhiishcherbakov.notebooks.config.OutboxEventProperties;
import com.serhiishcherbakov.notebooks.config.RabbitProperties;
import com.serhiishcherbakov.notebooks.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({SecurityProperties.class, RabbitProperties.class, OutboxEventProperties.class})
public class NotebooksServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotebooksServiceApplication.class, args);
    }

}
