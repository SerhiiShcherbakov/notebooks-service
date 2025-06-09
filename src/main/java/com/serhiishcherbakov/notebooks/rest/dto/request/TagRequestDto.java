package com.serhiishcherbakov.notebooks.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagRequestDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot be longer than 255 characters")
    private String title;
    @Pattern(
            regexp = "#[0-9a-f]{6}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid color format: must be a hexadecimal color code like '#RRGGBB'"
    )
    private String color;
}
