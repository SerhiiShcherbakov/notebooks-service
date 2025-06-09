package com.serhiishcherbakov.notebooks.domain.notebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class NotebookSearchFilter {
    private List<Long> tagIds;

    private NotebookSearchOrder order;
    private Boolean deleted;

    private int page;
    private int size = 20;

    public boolean hasTags() {
        return tagIds != null && !tagIds.isEmpty();
    }

    public NotebookSearchOrder getOrder() {
        return order == null ? NotebookSearchOrder.CREATED_AT_DESC : order;
    }

    public int getPage() {
        return Math.max(page, 0);
    }

    public int getSize() {
        return size <= 0 || size > 100 ? 20 : size;
    }

    @JsonIgnore
    public int getOffset() {
        return getPage() * getSize();
    }
}
