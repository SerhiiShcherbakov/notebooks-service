package com.serhiishcherbakov.notebooks.scheduler;

import com.serhiishcherbakov.notebooks.domain.notebook.NotebookService;
import com.serhiishcherbakov.notebooks.domain.outbox.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppScheduler {
    private final NotebookService notebookService;
    private final OutboxEventService outboxEventService;

    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public void cleanupDeletedNotebooks() {
        log.info("Starting notebook cleanup scheduler");
        var deletedNotebooks = notebookService.cleanupDeletedNotebooks();
        log.info("Finished notebook cleanup scheduler. Deleted notebooks: {}", deletedNotebooks.size());
    }

    @Scheduled(fixedDelayString = "${app.outbox.schedule-delay}")
    public void sendOutboxEvents() {
        log.info("Starting outbox events scheduler");
        outboxEventService.sendOutboxEvents();
        log.info("Finished outbox events scheduler");
    }
}
