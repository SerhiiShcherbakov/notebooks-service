package com.serhiishcherbakov.notebooks.domain.notebook;

import com.serhiishcherbakov.notebooks.domain.tag.TagService;
import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import com.serhiishcherbakov.notebooks.messaging.RabbitService;
import com.serhiishcherbakov.notebooks.security.UserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NotebookService.class)
class NotebookServiceTest {
    @Autowired
    private NotebookService notebookService;

    @MockitoBean
    private NotebookRepository notebookRepository;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private RabbitService rabbitService;

    @Test
    void should_throwNotFoundException_when_tagNotExist() {
        var user = UserDetails.builder().id("1").build();

        when(notebookRepository.findByIdAndUserId(100L, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notebookService.getNotebook(100L, user))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.NOTEBOOK_NOT_FOUND);
    }
}
