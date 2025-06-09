package com.serhiishcherbakov.notebooks.domain.tag;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.serhiishcherbakov.notebooks.config.TestContainersConfig;
import com.serhiishcherbakov.notebooks.domain.notebook.NotebookResultSetExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

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
@DataSet("tags/data.json")
class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;

    @Test
    void should_returnAllUserTags_when_userHasTags() {
        var tags = tagRepository.findAllByUserId("1");

        assertThat(tags)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .extracting(Tag::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void should_returnEmptyList_when_userHasNoTags() {
        var tags = tagRepository.findAllByUserId("100");

        assertThat(tags).isNotNull().isEmpty();
    }

    @Test
    void should_returnTagByIds() {
        var tags = tagRepository.findAllByIdInAndUserId(List.of(1L, 2L), "1");

        assertThat(tags).isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .extracting(Tag::getId)
                .contains(1L, 2L);
    }

    @Test
    void should_returnEmptyList_when_tagsDoesNotExist() {
        var tag = tagRepository.findByIdAndUserId(100L, "1");

        assertThat(tag).isEmpty();
    }

    @Test
    void should_mapAllEntityFields() {
        var tag = tagRepository.findByIdAndUserId(1L, "1").orElseThrow();

        assertThat(tag)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("title", "title-1")
                .hasFieldOrPropertyWithValue("color", "#111111")
                .hasFieldOrPropertyWithValue("userId", "1")
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void should_returnEmpty_when_tagDoesNotExist() {
        var tag = tagRepository.findByIdAndUserId(100L, "100");

        assertThat(tag).isEmpty();
    }

    @Test
    void should_returnEmpty_when_tagDoesNotBelongToUser() {
        var tag = tagRepository.findByIdAndUserId(1L, "100");

        assertThat(tag).isEmpty();
    }

    @Test
    @ExpectedDataSet("tags/after-tag-create.json")
    void should_createNewTag() {
        var tag = tagRepository.save(Tag.builder()
                .title("title-5")
                .color("#555555")
                .userId("1")
                .build());

        assertThat(tag).isNotNull()
                .extracting(Tag::getId)
                .matches(val -> val > 3L);
    }

    @Test
    @ExpectedDataSet("tags/after-tag-update.json")
    void should_updateExistingTag() {
        var tag = tagRepository.save(tagRepository.findByIdAndUserId(2L, "1").orElseThrow().toBuilder()
                .title("title-2-new")
                .color("#aaaaaa")
                .build());

        assertThat(tag).isNotNull();
    }

    @Test
    @ExpectedDataSet("tags/after-tag-delete-with-notebooks.json")
    void should_deleteTagAndNotebookTagLinks() {
        tagRepository.delete(tagRepository.findByIdAndUserId(2L, "1").orElseThrow());
    }

    @Test
    @ExpectedDataSet("tags/after-tag-delete-no-notebooks.json")
    void should_notThrowException_when_deletingTagHasNoNotebookTagLinks() {
        tagRepository.delete(tagRepository.findByIdAndUserId(3L, "1").orElseThrow());
    }

    @Test
    void should_notThrowException_when_deletingTagDoesNotExist() {
        tagRepository.delete(Tag.builder().id(100L).build());
    }
}
