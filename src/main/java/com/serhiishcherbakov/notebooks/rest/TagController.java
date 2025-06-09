package com.serhiishcherbakov.notebooks.rest;

import com.serhiishcherbakov.notebooks.domain.tag.TagService;
import com.serhiishcherbakov.notebooks.rest.dto.request.TagRequestDto;
import com.serhiishcherbakov.notebooks.rest.dto.response.TagDto;
import com.serhiishcherbakov.notebooks.rest.dto.response.TagsDto;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.serhiishcherbakov.notebooks.security.UserInterceptor.USER_DETAILS_ATTRIBUTE;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public TagsDto getTags(@RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return TagsDto.of(tagService.getTags(user));
    }

    @GetMapping("/{id}")
    public TagDto getTag(@PathVariable Long id,
                         @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return TagDto.of(tagService.getTag(id, user));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TagDto createTag(@Valid @RequestBody TagRequestDto tag,
                            @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return TagDto.of(tagService.createTag(tag, user));
    }

    @PutMapping("/{id}")
    public TagDto updateTag(@PathVariable Long id,
                            @Valid @RequestBody TagRequestDto tag,
                            @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return TagDto.of(tagService.updateTag(id, tag, user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id, @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        tagService.deleteTag(id, user);
    }
}
