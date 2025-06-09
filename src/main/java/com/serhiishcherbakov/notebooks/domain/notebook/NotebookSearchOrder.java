package com.serhiishcherbakov.notebooks.domain.notebook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotebookSearchOrder {
    BEST_MATCH_ASC("n.title", "ASC"),
    BEST_MATCH_DESC("n.title", "DESC"),
    TITLE_ASC("n.title", "ASC"),
    TITLE_DESC("n.title", "DESC"),
    CREATED_AT_ASC("n.created_at", "ASC"),
    CREATED_AT_DESC("n.created_at", "DESC"),
    UPDATED_AT_ASC("n.updated_at", "ASC"),
    UPDATED_AT_DESC("n.updated_at", "DESC");

    private final String column;
    private final String direction;
}
