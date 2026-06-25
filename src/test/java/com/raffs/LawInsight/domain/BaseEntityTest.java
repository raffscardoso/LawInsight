package com.raffs.LawInsight.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BaseEntityTest {

    @Autowired
    private TestAuditEntityRepository repository;

    @Test
    void shouldPopulateAuditFieldsOnPersist() {
        var entity = new TestAuditEntity();
        entity.setName("test-audit");

        var saved = repository.save(entity);

        assertThat(saved.getId())
                .as("ID must be auto-generated on persist")
                .isNotNull();
        assertThat(saved.getVersion())
                .as("Version must be initialized on persist")
                .isNotNull();
        assertThat(saved.getCreatedAt())
                .as("createdAt must be set by auditing listener")
                .isNotNull();
        assertThat(saved.getLastModifiedAt())
                .as("lastModifiedAt must be set by auditing listener")
                .isNotNull();
    }

    @Test
    void shouldIncrementVersionOnUpdate() {
        var entity = new TestAuditEntity();
        entity.setName("version-test");
        var saved = repository.save(entity);
        saved.setName("testEntity");
        var versionBefore = saved.getVersion();

        var updated = repository.save(saved);

        assertThat(updated.getVersion())
                .as("Version must increment on each update")
                .isEqualTo(versionBefore + 1);
    }

    @Test
    void shouldAutoPopulateCreatedByOnPersist() {
        var entity = new TestAuditEntity();
        entity.setName("created-by-test");
        var saved = repository.save(entity);

        assertThat(saved.getCreatedBy())
                .as("createdBy must be auto-populated on persist")
                .isEqualTo("SYSTEM");
        assertThat(saved.getLastModifiedBy())
                .as("lastModifiedBy must be auto-populated on persist")
                .isEqualTo("SYSTEM");
    }
}
