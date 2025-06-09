package com.serhiishcherbakov.notebooks.domain.tag;

import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import com.serhiishcherbakov.notebooks.messaging.RabbitService;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TagService.class)
class TagServiceTest {
    @Autowired
    private TagService tagService;

    @MockitoBean
    private TagRepository tagRepository;

    @MockitoBean
    private RabbitService rabbitService;

    @Test
    void should_returnEmptyList_whenTagIdsIsNull() {
        var tags = tagService.getTags(null, UserDetails.builder().id("1").build());

        assertThat(tags).isNotNull().isEmpty();
    }

    @Test
    void should_throwNotFoundException_when_tagNotExist() {
        var user = UserDetails.builder().id("1").build();

        when(tagRepository.findByIdAndUserId(100L, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> tagService.getTag(100L, user))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.TAG_NOT_FOUND);
    }
}
