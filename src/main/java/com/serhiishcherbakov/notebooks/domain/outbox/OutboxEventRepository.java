package com.serhiishcherbakov.notebooks.domain.outbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutboxEventRepository {
    private final JdbcClient jdbcClient;
    private final SimpleJdbcInsert insertOutboxEvent;

    private final RowMapper<OutboxEvent> outboxEventRowMapper = (rs, rowNum) -> OutboxEvent.builder()
            .id(rs.getLong("id"))
            .type(OutboxEventType.valueOf(rs.getString("type")))
            .processed(rs.getBoolean("processed"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .attempts(rs.getInt("attempts"))
            .data(rs.getString("data"))
            .build();

    public OutboxEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
        this.insertOutboxEvent = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("outbox_events")
                .usingColumns("type", "processed", "data")
                .usingGeneratedKeyColumns("id");
    }

    public void addAll(List<OutboxEvent> events) {
        MapSqlParameterSource[] batchParams = events.stream()
                .map(event -> new MapSqlParameterSource()
                        .addValue("type", event.getType())
                        .addValue("processed", event.isProcessed())
                        .addValue("data", event.getData()))
                .toArray(MapSqlParameterSource[]::new);

        insertOutboxEvent.executeBatch(batchParams);
    }

    public List<OutboxEvent> findUnprocessedEventsToPublish(int maxRetryAttempts, int batchSize) {
        return jdbcClient.sql("""
                        SELECT id, type, processed, created_at, attempts, data
                        FROM outbox_events
                        WHERE processed = false AND attempts < :attempts
                        ORDER BY created_at
                        LIMIT :limit
                        FOR UPDATE SKIP LOCKED
                        """)
                .param("attempts", maxRetryAttempts)
                .param("limit", batchSize)
                .query(outboxEventRowMapper)
                .list();
    }

    public OutboxEvent update(OutboxEvent event) {
        jdbcClient.sql("""
                        UPDATE outbox_events
                        SET type = :type,
                            processed = :processed,
                            attempts = :attempts,
                            data = :data
                        WHERE id = :id
                        """)
                .paramSource(createParameterSourceForUpdate(event))
                .update();
        return event;
    }

    private MapSqlParameterSource createParameterSourceForUpdate(OutboxEvent event) {
        return new MapSqlParameterSource()
                .addValue("type", event.getType().name())
                .addValue("processed", event.isProcessed())
                .addValue("attempts", event.getAttempts())
                .addValue("data", event.getData())
                .addValue("id", event.getId());
    }
}
