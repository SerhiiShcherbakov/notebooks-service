package com.serhiishcherbakov.notebooks.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagIdsRequestDto {
    @NotEmpty(message = "Tags cannot be empty")
    private final List<Long> tagIds;
}
