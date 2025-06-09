package com.serhiishcherbakov.notebooks.rest.dto.response;

import com.serhiishcherbakov.notebooks.domain.common.PageResult;
import com.serhiishcherbakov.notebooks.domain.notebook.Notebook;

import java.util.List;

public record NotebooksDto(List<NotebookDto> notebooks, PaginatorDto paginator) {
    public static NotebooksDto of(PageResult<Notebook> notebookPage) {
        return new NotebooksDto(NotebookDto.of(notebookPage.getContent()),
                new PaginatorDto(notebookPage.getPage(), notebookPage.getSize(), notebookPage.getTotal()));
    }
}
