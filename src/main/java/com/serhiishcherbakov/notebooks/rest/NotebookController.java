package com.serhiishcherbakov.notebooks.rest;

import com.serhiishcherbakov.notebooks.domain.notebook.NotebookSearchFilter;
import com.serhiishcherbakov.notebooks.domain.notebook.NotebookService;
import com.serhiishcherbakov.notebooks.rest.dto.request.NotebookRequestDto;
import com.serhiishcherbakov.notebooks.rest.dto.request.TagIdsRequestDto;
import com.serhiishcherbakov.notebooks.rest.dto.response.NotebookDto;
import com.serhiishcherbakov.notebooks.rest.dto.response.NotebooksDto;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.serhiishcherbakov.notebooks.security.UserInterceptor.USER_DETAILS_ATTRIBUTE;

@RestController
@RequestMapping("/notebooks")
@RequiredArgsConstructor
public class NotebookController {
    private final NotebookService notebookService;

    @GetMapping
    public NotebooksDto getNotebooks(NotebookSearchFilter filter,
                                     @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebooksDto.of(notebookService.searchNotebooks(filter, user));
    }

    @GetMapping("/{id}")
    public NotebookDto getNotebook(@PathVariable Long id,
                                   @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.getNotebook(id, user));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public NotebookDto createNotebook(@Valid @RequestBody NotebookRequestDto notebook,
                                      @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.createNotebook(notebook, user));
    }

    @PutMapping("/{id}")
    public NotebookDto updateNotebook(@PathVariable Long id,
                                      @Valid @RequestBody NotebookRequestDto notebook,
                                      @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.updateNotebook(id, notebook, user));
    }

    @PostMapping("/{id}/add-tags")
    public NotebookDto addTags(@PathVariable Long id,
                               @Valid @RequestBody TagIdsRequestDto request,
                               @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.addTagsToNotebook(id, request, user));
    }

    @PostMapping("/{id}/remove-tags")
    public NotebookDto removeTags(@PathVariable Long id,
                                  @Valid @RequestBody TagIdsRequestDto request,
                                  @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.removeTagsFromNotebook(id, request, user));
    }

    @PostMapping("/{id}/restore")
    public NotebookDto restoreNotebook(@PathVariable Long id,
                                       @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.restoreNotebook(id, user));
    }

    @PostMapping("/{id}/soft-delete")
    public NotebookDto softDeleteNotebook(@PathVariable Long id,
                                          @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        return NotebookDto.of(notebookService.softDeleteNotebook(id, user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteNotebook(@PathVariable Long id, @RequestAttribute(USER_DETAILS_ATTRIBUTE) UserDetails user) {
        notebookService.deleteNotebook(id, user);
    }
}
