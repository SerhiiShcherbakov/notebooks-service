package com.serhiishcherbakov.notebooks.rest.dto.response;

import com.serhiishcherbakov.notebooks.domain.tag.Tag;

import java.util.List;

public record TagsDto(List<TagDto> tags) {
    public static TagsDto of(List<Tag> tags) {
        return new TagsDto(TagDto.of(tags));
    }
}
