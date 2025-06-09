package com.serhiishcherbakov.notebooks.rest.dto.response;

import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record NotebookDto(Long id,
                          String title,
                          String body,
                          List<TagDto> tags,
                          String userId,
                          Instant createdAt,
                          Instant updatedAt,
                          Instant deletedAt) {

    public static List<NotebookDto> of(List<Notebook> notebooks) {
        if (notebooks == null) {
            return Collections.emptyList();
        }
        return notebooks.stream().map(NotebookDto::of).toList();
    }

    public static NotebookDto of(Notebook notebook) {
        return new NotebookDto(
                notebook.getId(),
                notebook.getTitle(),
                notebook.getBody(),
                TagDto.of(notebook.getTags()),
                notebook.getUserId(),
                notebook.getCreatedAt(),
                notebook.getUpdatedAt(),
                notebook.getDeletedAt()
        );
    }
}
