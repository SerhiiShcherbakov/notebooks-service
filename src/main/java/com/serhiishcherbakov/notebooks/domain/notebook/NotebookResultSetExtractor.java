package com.serhiishcherbakov.notebooks.domain.notebook;

import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

@Component
public class NotebookResultSetExtractor implements ResultSetExtractor<List<Notebook>> {
    @Override
    public List<Notebook> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Notebook> notebooks = new LinkedHashMap<>();

        while (rs.next()) {
            var notebookId = rs.getLong("notebook_id");

            var notebook = notebooks.get(notebookId);
            if (notebook == null) {
                notebook = extractNotebook(rs);
                notebooks.put(notebookId, notebook);
            }

            if (rs.getLong("tag_id") != 0) {
                notebook.getTags().add(extractTag(rs));
            }
        }

        return notebooks.values().stream().toList();
    }

    private Notebook extractNotebook(ResultSet rs) throws SQLException, DataAccessException {
        return Notebook.builder()
                .id(rs.getLong("notebook_id"))
                .title(rs.getString("notebook_title"))
                .body(rs.getString("notebook_body"))
                .userId(rs.getString("notebook_user_id"))
                .tags(new ArrayList<>())
                .deletedAt(extractDeletedAt(rs))
                .createdAt(rs.getTimestamp("notebook_created_at").toInstant())
                .updatedAt(rs.getTimestamp("notebook_updated_at").toInstant())
                .build();
    }

    private Instant extractDeletedAt(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.getTimestamp("notebook_deleted_at") == null) {
            return null;
        }
        return rs.getTimestamp("notebook_deleted_at").toInstant();
    }

    private Tag extractTag(ResultSet rs) throws SQLException, DataAccessException {
        return Tag.builder()
                .id(rs.getLong("tag_id"))
                .title(rs.getString("tag_title"))
                .color(rs.getString("tag_color"))
                .userId(rs.getString("tag_user_id"))
                .createdAt(rs.getTimestamp("tag_created_at").toInstant())
                .updatedAt(rs.getTimestamp("tag_updated_at").toInstant())
                .build();
    }
}
