package com.serhiishcherbakov.notebooks.domain.notebook;

import com.serhiishcherbakov.notebooks.domain.common.PageResult;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class NotebookRepository {
    private final JdbcClient jdbcClient;
    private final SimpleJdbcInsert insertNotebook;
    private final SimpleJdbcInsert insertNotebookTag;
    private final NotebookResultSetExtractor notebookResultSetExtractor;

    public NotebookRepository(JdbcTemplate jdbcTemplate,
                              NotebookResultSetExtractor notebookResultSetExtractor) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);

        this.insertNotebook = new SimpleJdbcInsert(jdbcTemplate).withTableName("notebooks")
                .usingColumns("title", "body", "user_id")
                .usingGeneratedKeyColumns("id");

        this.insertNotebookTag = new SimpleJdbcInsert(jdbcTemplate).withTableName("notebooks_tags");

        this.notebookResultSetExtractor = notebookResultSetExtractor;
    }

    public PageResult<Notebook> findAllByFilterAndUserId(NotebookSearchFilter filter, String userId) {
        var filteredNotebookIds = findNotebookIdsByFilterOrdered(filter, userId);
        if (filteredNotebookIds.isEmpty()) {
            return PageResult.<Notebook>builder()
                    .content(List.of())
                    .page(filter.getPage())
                    .size(filter.getSize())
                    .build();
        }

        var sql = String.format("""
                SELECT n.id AS notebook_id,
                       n.title AS notebook_title,
                       n.body AS notebook_body,
                       n.user_id AS notebook_user_id,
                       n.created_at AS notebook_created_at,
                       n.updated_at AS notebook_updated_at,
                       n.deleted_at AS notebook_deleted_at,
                       t.id AS tag_id,
                       t.title AS tag_title,
                       t.color AS tag_color,
                       t.user_id AS tag_user_id,
                       t.created_at AS tag_created_at,
                       t.updated_at AS tag_updated_at
                FROM notebooks n
                LEFT JOIN notebooks_tags nt on n.id = nt.notebook_id
                LEFT JOIN tags t on nt.tag_id = t.id
                WHERE n.id IN (:notebook_ids)
                ORDER BY %s %s
                """, filter.getOrder().getColumn(), filter.getOrder().getDirection());

        var notebooks = jdbcClient.sql(sql)
                .param("notebook_ids", filteredNotebookIds.stream().skip(filter.getOffset()).limit(filter.getSize()).toList())
                .query(notebookResultSetExtractor);

        return PageResult.<Notebook>builder()
                .content(notebooks)
                .page(filter.getPage())
                .size(filter.getSize())
                .total(filteredNotebookIds.size())
                .build();
    }

    private List<Long> findNotebookIdsByFilterOrdered(NotebookSearchFilter filter, String userId) {
        var sql = new StringBuilder("""
                SELECT n.id
                FROM notebooks n
                """);

        if (filter.hasTags()) {
            sql.append("""
                    INNER JOIN (
                        SELECT notebook_id
                        FROM notebooks_tags
                        WHERE tag_id IN (:tag_ids)
                        GROUP BY notebook_id
                        HAVING COUNT(DISTINCT tag_id) = :tag_ids_count
                    ) nt ON n.id = nt.notebook_id
                    """);
        }

        sql.append(" WHERE n.user_id = :user_id");

        if (filter.getDeleted() != null) {
            sql.append(" AND n.deleted_at IS ").append(filter.getDeleted() ? "NOT NULL" : "NULL");
        }

        sql.append(String.format(" ORDER BY %s %s", filter.getOrder().getColumn(), filter.getOrder().getDirection()));

        var sqlParams = new MapSqlParameterSource().addValue("user_id", userId);
        if (filter.hasTags()) {
            sqlParams.addValue("tag_ids", filter.getTagIds());
            sqlParams.addValue("tag_ids_count", filter.getTagIds().size());
        }

        return jdbcClient.sql(sql.toString())
                .paramSource(sqlParams)
                .query(Long.class)
                .list();
    }

    public List<Notebook> findAllByDeletedAtBefore(Instant deletedAtBefore) {
        return jdbcClient.sql("""
                        SELECT n.id AS notebook_id,
                               n.title AS notebook_title,
                               n.body AS notebook_body,
                               n.user_id AS notebook_user_id,
                               n.created_at AS notebook_created_at,
                               n.updated_at AS notebook_updated_at,
                               n.deleted_at AS notebook_deleted_at,
                               t.id AS tag_id,
                               t.title AS tag_title,
                               t.color AS tag_color,
                               t.user_id AS tag_user_id,
                               t.created_at AS tag_created_at,
                               t.updated_at AS tag_updated_at
                        FROM notebooks n
                            LEFT JOIN notebooks_tags nt ON n.id = nt.notebook_id
                            LEFT JOIN tags t ON nt.tag_id = t.id
                        WHERE n.deleted_at IS NOT NULL AND n.deleted_at < :deleted_at
                        """)
                .param("deleted_at", deletedAtBefore)
                .query(notebookResultSetExtractor)
                .stream()
                .toList();
    }

    public Optional<Notebook> findByIdAndUserId(Long id, String userId) {
        return jdbcClient.sql("""
                        SELECT n.id AS notebook_id,
                               n.title AS notebook_title,
                               n.body AS notebook_body,
                               n.user_id AS notebook_user_id,
                               n.created_at AS notebook_created_at,
                               n.updated_at AS notebook_updated_at,
                               n.deleted_at AS notebook_deleted_at,
                               t.id AS tag_id,
                               t.title AS tag_title,
                               t.color AS tag_color,
                               t.user_id AS tag_user_id,
                               t.created_at AS tag_created_at,
                               t.updated_at AS tag_updated_at
                        FROM notebooks n
                            LEFT JOIN notebooks_tags nt ON n.id = nt.notebook_id
                            LEFT JOIN tags t ON nt.tag_id = t.id
                        WHERE n.id = :id AND n.user_id = :user_id
                        """)
                .param("id", id)
                .param("user_id", userId)
                .query(notebookResultSetExtractor)
                .stream()
                .findFirst();
    }

    public Notebook save(Notebook notebook) {
        if (notebook.isNew()) {
            return createNotebook(notebook);
        } else {
            return updateNotebook(notebook);
        }
    }

    private Notebook createNotebook(Notebook notebook) {
        Long id = insertNotebook.executeAndReturnKey(createNotebookParameterSource(notebook)).longValue();

        if (notebook.hasTags()) {
            insertNotebookTag.executeBatch(createNotebooksTagsParameterSource(id, notebook.getTags()));
        }

        return findByIdAndUserId(id, notebook.getUserId()).orElseThrow();
    }

    private Notebook updateNotebook(Notebook notebook) {
        jdbcClient.sql("""
                        UPDATE notebooks
                        SET title = :title,
                            body = :body,
                            deleted_at = :deleted_at
                        WHERE id = :id
                        """)
                .paramSource(createNotebookParameterSource(notebook))
                .update();

        jdbcClient.sql("DELETE FROM notebooks_tags WHERE notebook_id = :id").param("id", notebook.getId()).update();

        if (notebook.hasTags()) {
            insertNotebookTag.executeBatch(createNotebooksTagsParameterSource(notebook.getId(), notebook.getTags()));
        }

        return findByIdAndUserId(notebook.getId(), notebook.getUserId()).orElseThrow();
    }

    private MapSqlParameterSource createNotebookParameterSource(Notebook notebook) {
        return new MapSqlParameterSource()
                .addValue("id", notebook.getId())
                .addValue("title", notebook.getTitle())
                .addValue("body", notebook.getBody())
                .addValue("user_id", notebook.getUserId())
                .addValue("deleted_at", notebook.getDeletedAt());
    }

    private MapSqlParameterSource[] createNotebooksTagsParameterSource(Long notebookId, List<Tag> tags) {
        return tags.stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("notebook_id", notebookId)
                        .addValue("tag_id", tag.getId()))
                .toArray(MapSqlParameterSource[]::new);
    }

    public void delete(Notebook notebook) {
        jdbcClient.sql("DELETE FROM notebooks WHERE id = :id").param("id", notebook.getId()).update();
    }

    public void deleteAll(List<Notebook> notebooks) {
        jdbcClient.sql("DELETE FROM notebooks WHERE id IN (:ids)")
                .param("ids", notebooks.stream().map(Notebook::getId).toList())
                .update();
    }
}
