package com.serhiishcherbakov.notebooks.domain.tag;

import com.serhiishcherbakov.notebooks.domain.outbox.OutboxEventService;
import com.serhiishcherbakov.notebooks.domain.outbox.OutboxEventType;
import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import com.serhiishcherbakov.notebooks.rest.dto.request.TagRequestDto;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final OutboxEventService outboxEventService;

    @Transactional(readOnly = true)
    public List<Tag> getTags(UserDetails user) {
        return tagRepository.findAllByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Tag> getTags(List<Long> ids, UserDetails user) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return tagRepository.findAllByIdInAndUserId(ids, user.getId());
    }

    public Tag getTag(Long id, UserDetails user) {
        return tagRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new AppException(Error.TAG_NOT_FOUND));
    }

    @Transactional
    public Tag createTag(TagRequestDto tagRequest, UserDetails user) {
        var tag = Tag.builder()
                .title(tagRequest.getTitle())
                .color(tagRequest.getColor())
                .userId(user.getId())
                .build();

        tag = tagRepository.save(tag);
        outboxEventService.saveTagOutboxEvent(tag, OutboxEventType.TAG_CREATED);
        return tag;
    }

    @Transactional
    public Tag updateTag(Long id, TagRequestDto tagRequest, UserDetails user) {
        var tag = getTag(id, user).toBuilder()
                .title(tagRequest.getTitle())
                .color(tagRequest.getColor())
                .build();

        tag = tagRepository.save(tag);
        outboxEventService.saveTagOutboxEvent(tag, OutboxEventType.TAG_UPDATED);
        return tag;
    }

    @Transactional
    public void deleteTag(Long id, UserDetails user) {
        var tag = getTag(id, user);
        tagRepository.delete(tag);
        outboxEventService.saveTagOutboxEvent(tag, OutboxEventType.TAG_DELETED);
    }
}
