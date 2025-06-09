package com.serhiishcherbakov.notebooks.domain.tag;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepository {
    private final JdbcClient jdbcClient;
    private final SimpleJdbcInsert tagInsert;

    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> Tag.builder()
            .id(rs.getLong("tag_id"))
            .title(rs.getString("tag_title"))
            .color(rs.getString("tag_color"))
            .userId(rs.getString("tag_user_id"))
            .createdAt(rs.getTimestamp("tag_created_at").toInstant())
            .updatedAt(rs.getTimestamp("tag_updated_at").toInstant())
            .build();

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
        this.tagInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tags")
                .usingColumns("title", "color", "user_id")
                .usingGeneratedKeyColumns("id");
    }

    public List<Tag> findAllByUserId(String userId) {
        return jdbcClient.sql("""
                        SELECT t.id tag_id,
                               t.title tag_title,
                               t.color tag_color,
                               t.user_id tag_user_id,
                               t.created_at tag_created_at,
                               t.updated_at tag_updated_at
                        FROM tags t
                        WHERE t.user_id = :user_id
                        """)
                .param("user_id", userId)
                .query(tagRowMapper)
                .list();
    }

    public List<Tag> findAllByIdInAndUserId(List<Long> ids, String userId) {
        return jdbcClient.sql("""
                        SELECT t.id tag_id,
                               t.title tag_title,
                               t.color tag_color,
                               t.user_id tag_user_id,
                               t.created_at tag_created_at,
                               t.updated_at tag_updated_at
                        FROM tags t
                        WHERE t.user_id = :user_id AND t.id IN (:ids)
                        """)
                .param("user_id", userId)
                .param("ids", ids)
                .query(tagRowMapper)
                .list();
    }

    public Optional<Tag> findByIdAndUserId(Long id, String userId) {
        return jdbcClient.sql("""
                        SELECT t.id tag_id,
                               t.title tag_title,
                               t.color tag_color,
                               t.user_id tag_user_id,
                               t.created_at tag_created_at,
                               t.updated_at tag_updated_at
                        FROM tags t
                        WHERE t.user_id = :user_id AND t.id = :id
                        """)
                .param("user_id", userId)
                .param("id", id)
                .query(tagRowMapper)
                .optional();
    }

    public Tag save(Tag tag) {
        if (tag.isNew()) {
            Long tagId = tagInsert.executeAndReturnKey(createTagParameterSource(tag)).longValue();
            return findByIdAndUserId(tagId, tag.getUserId()).orElseThrow();
        } else {
            return updateTag(tag);
        }
    }

    private Tag updateTag(Tag tag) {
        jdbcClient.sql("""
                        UPDATE tags
                        SET title = :title,
                            color = :color
                        WHERE id = :id
                        """)
                .paramSource(createTagParameterSource(tag))
                .update();

        return findByIdAndUserId(tag.getId(), tag.getUserId()).orElseThrow();
    }

    private MapSqlParameterSource createTagParameterSource(Tag tag) {
        return new MapSqlParameterSource()
                .addValue("id", tag.getId())
                .addValue("title", tag.getTitle())
                .addValue("user_id", tag.getUserId())
                .addValue("color", tag.getColor());
    }

    public void delete(Tag tag) {
        jdbcClient.sql("DELETE FROM tags WHERE id = :id")
                .param("id", tag.getId())
                .update();
    }
}
