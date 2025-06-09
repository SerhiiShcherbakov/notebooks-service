package com.serhiishcherbakov.notebooks.domain.notebook;

import com.serhiishcherbakov.notebooks.domain.common.PageResult;
import com.serhiishcherbakov.notebooks.domain.tag.TagService;
import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import com.serhiishcherbakov.notebooks.messaging.RabbitService;
import com.serhiishcherbakov.notebooks.rest.dto.request.NotebookRequestDto;
import com.serhiishcherbakov.notebooks.rest.dto.request.TagIdsRequestDto;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final TagService tagService;
    private final RabbitService rabbitService;

    @Value("${app.notebooks.retention-period}")
    private Duration retentionPeriod;

    @Transactional(readOnly = true)
    public PageResult<Notebook> searchNotebooks(NotebookSearchFilter filter, UserDetails user) {
        return notebookRepository.findAllByFilterAndUserId(filter, user.getId());
    }

    public Notebook getNotebook(Long id, UserDetails user) {
        return notebookRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new AppException(Error.NOTEBOOK_NOT_FOUND));
    }

    @Transactional
    public Notebook createNotebook(NotebookRequestDto notebookRequest, UserDetails user) {
        var notebook = Notebook.builder()
                .title(notebookRequest.getTitle())
                .body(notebookRequest.getBody())
                .userId(user.getId())
                .tags(tagService.getTags(notebookRequest.getTagIds(), user))
                .build();

        notebook = notebookRepository.save(notebook);
        rabbitService.publishNotebookCreatedEvent(notebook);
        return notebook;
    }

    @Transactional
    public Notebook updateNotebook(Long id, NotebookRequestDto notebookRequest, UserDetails user) {
        var notebook = getNotebook(id, user).toBuilder()
                .title(notebookRequest.getTitle())
                .body(notebookRequest.getBody())
                .tags(tagService.getTags(notebookRequest.getTagIds(), user))
                .build();

        return updateAndPublishEvent(notebook);
    }

    @Transactional
    public Notebook addTagsToNotebook(Long id, TagIdsRequestDto request, UserDetails user) {
        var tags = tagService.getTags(request.getTagIds(), user);
        var notebook = getNotebook(id, user);
        if (tags.isEmpty()) {
            return notebook;
        }

        tags.removeIf(tag -> {
            for (var entityTag : notebook.getTags()) {
                if (entityTag.getId().equals(tag.getId())) {
                    return true;
                }
            }
            return false;
        });
        var newTags = Stream.concat(notebook.getTags().stream(), tags.stream()).toList();
        return updateAndPublishEvent(notebook.toBuilder().tags(newTags).build());
    }

    @Transactional
    public Notebook removeTagsFromNotebook(Long id, TagIdsRequestDto request, UserDetails user) {
        var notebook = getNotebook(id, user);
        if (!notebook.hasTags()) {
            return notebook;
        }

        var newTags = notebook.getTags().stream()
                .filter(tag -> !request.getTagIds().contains(tag.getId()))
                .toList();
        return updateAndPublishEvent(notebook.toBuilder().tags(newTags).build());
    }

    @Transactional
    public Notebook restoreNotebook(Long id, UserDetails user) {
        var notebook = getNotebook(id, user).toBuilder()
                .deletedAt(null)
                .build();

        return updateAndPublishEvent(notebook);
    }

    @Transactional
    public Notebook softDeleteNotebook(Long id, UserDetails user) {
        var notebook = getNotebook(id, user).toBuilder()
                .deletedAt(Instant.now())
                .build();

        return updateAndPublishEvent(notebook);
    }

    private Notebook updateAndPublishEvent(Notebook notebook) {
        notebook = notebookRepository.save(notebook);
        rabbitService.publishNotebookUpdatedEvent(notebook);
        return notebook;
    }

    @Transactional
    public void deleteNotebook(Long id, UserDetails user) {
        var notebook = getNotebook(id, user);
        notebookRepository.delete(notebook);
        rabbitService.publishNotebookDeletedEvent(notebook);
    }

    @Transactional
    public List<Notebook> cleanupDeletedNotebooks() {
        var deletedAtBefore = Instant.now().minus(retentionPeriod);
        var notebooks = notebookRepository.findAllByDeletedAtBefore(deletedAtBefore);
        if (notebooks.isEmpty()) {
            return notebooks;
        }
        notebookRepository.deleteAll(notebooks);
        rabbitService.publishNotebookDeletedEvent(notebooks.toArray(Notebook[]::new));
        return notebooks;
    }
}
