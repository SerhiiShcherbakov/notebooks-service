package com.serhiishcherbakov.notebooks.domain.notebook;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.serhiishcherbakov.notebooks.config.TestContainersConfig;
import com.serhiishcherbakov.notebooks.domain.common.PageResult;
import com.serhiishcherbakov.notebooks.domain.tag.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(
        includeFilters = {
                @ComponentScan.Filter(Repository.class),
                @ComponentScan.Filter(classes = NotebookResultSetExtractor.class, type = FilterType.ASSIGNABLE_TYPE)
        }
)
@Import(TestContainersConfig.class)
@DBRider
@DataSet("notebooks/data.json")
class NotebookRepositoryTest {
    @Autowired
    private NotebookRepository notebookRepository;

    @Test
    void should_returnAllUserNotebooks() {
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(new NotebookSearchFilter(), "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(6);

        assertThat(notebooksPage.getContent())
                .hasSize(6)
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Test
    void should_returnEmptyList_when_nothingFound() {
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(new NotebookSearchFilter(), "100");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(0);

        assertThat(notebooksPage.getContent()).isEmpty();
    }

    @Test
    void should_filterBySingleTagId() {
        var filter = new NotebookSearchFilter();
        filter.setTagIds(List.of(1L));
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(2);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(1L, 4L);
    }

    @Test
    void should_filterByMultipleTagIds() {
        var filter = new NotebookSearchFilter();
        filter.setTagIds(List.of(1L, 2L));
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(2);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(1L, 4L);
    }

    @Test
    void should_returnOnlyDeleted() {
        var filter = new NotebookSearchFilter();
        filter.setDeleted(true);
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(2);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(2L, 4L);
    }

    @Test
    void should_returnOnlyNotDeleted() {
        var filter = new NotebookSearchFilter();
        filter.setDeleted(false);
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(4);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(1L, 3L, 5L, 6L);
    }

    @Test
    void should_returnSecondPage() {
        var filter = new NotebookSearchFilter();
        filter.setPage(1);
        filter.setSize(2);
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage.getPage()).isEqualTo(1);
        assertThat(notebooksPage.getSize()).isEqualTo(2);
        assertThat(notebooksPage.getTotal()).isEqualTo(6);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(4L, 5L);
    }

    @Test
    void should_orderByTitle() {
        var filter = new NotebookSearchFilter();
        filter.setOrder(NotebookSearchOrder.TITLE_ASC);
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(6);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .contains(6L, 5L, 4L, 3L, 2L, 1L);
    }

    @Test
    void should_filterByAllFields() {
        var filter = new NotebookSearchFilter();
        filter.setTagIds(List.of(1L, 2L));
        filter.setDeleted(true);
        var notebooksPage = notebookRepository.findAllByFilterAndUserId(filter, "1");

        assertThat(notebooksPage).isNotNull()
                .extracting(PageResult::getTotal).isEqualTo(1);

        assertThat(notebooksPage.getContent())
                .isNotEmpty()
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(4L);
    }

    @Test
    void should_mapAllNotebookFields() {
        var notebook = notebookRepository.findByIdAndUserId(4L, "1").orElseThrow();

        assertThat(notebook)
                .hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("title", "title-4")
                .hasFieldOrPropertyWithValue("body", "body-4")
                .hasFieldOrPropertyWithValue("userId", "1")
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void should_returnAllDeletedBeforeDate() {
        var notebooks = notebookRepository.findAllByDeletedAtBefore(Instant.parse("2025-01-15T00:00:00Z"));

        assertThat(notebooks)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .extracting(Notebook::getId)
                .containsExactlyInAnyOrder(4L);
    }

    @Test
    void should_returnEmpty_when_userIsNotTheOwner() {
        var notebook = notebookRepository.findByIdAndUserId(7L, "100");

        assertThat(notebook).isEmpty();
    }

    @Test
    @ExpectedDataSet("notebooks/after-notebook-create.json")
    void should_createNotebookAndTagLinks() {
        var notebook = notebookRepository.save(Notebook.builder()
                .title("title-8")
                .body("body-8")
                .userId("1")
                .tags(List.of(Tag.builder().id(1L).build(), Tag.builder().id(2L).build()))
                .build());

        assertThat(notebook).isNotNull()
                .extracting(Notebook::getId)
                .matches(v -> v > 7L);
    }

    @Test
    @ExpectedDataSet("notebooks/after-notebook-create-empty-tags.json")
    void should_createNotebookWithEmptyTagLinks() {
        var notebook = notebookRepository.save(Notebook.builder()
                .title("title-8")
                .body("body-8")
                .userId("1")
                .tags(null)
                .build());

        assertThat(notebook).isNotNull()
                .extracting(Notebook::getId)
                .matches(v -> v > 7L);
    }

    @Test
    @ExpectedDataSet("notebooks/after-notebook-update.json")
    void should_updateNotebookAndTagLinks() {
        var notebook = notebookRepository.save(notebookRepository.findByIdAndUserId(1L, "1").orElseThrow().toBuilder()
                .title("title-1-new")
                .body("body-1-new")
                .deletedAt(Instant.now())
                .tags(List.of(Tag.builder().id(1L).build(), Tag.builder().id(3L).build()))
                .build());

        assertThat(notebook).isNotNull();
    }

    @Test
    @ExpectedDataSet("notebooks/after-notebook-delete.json")
    void should_deleteNotebookAndTagLinks() {
        notebookRepository.delete(notebookRepository.findByIdAndUserId(1L, "1").orElseThrow());
    }

    @Test
    void should_notThrowException_when_deletingTagDoesNotExist() {
        notebookRepository.delete(Notebook.builder().id(100L).build());
    }

    @Test
    @ExpectedDataSet("notebooks/after-notebooks-delete.json")
    void should_deleteNotebooksAndTagLinks() {
        var notebooks = List.of(
                notebookRepository.findByIdAndUserId(1L, "1").orElseThrow(),
                notebookRepository.findByIdAndUserId(4L, "1").orElseThrow(),
                notebookRepository.findByIdAndUserId(7L, "2").orElseThrow()
        );

        notebookRepository.deleteAll(notebooks);
    }
}
