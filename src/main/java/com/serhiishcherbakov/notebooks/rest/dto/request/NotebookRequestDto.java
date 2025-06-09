package com.serhiishcherbakov.notebooks.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotebookRequestDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot be longer than 255 characters")
    private String title;
    @Size(max = 65535, message = "Body cannot be longer than 65535 characters")
    private String body;
    private List<Long> tagIds;
}
