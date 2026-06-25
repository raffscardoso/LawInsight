package com.raffs.LawInsight.config;

import com.raffs.LawInsight.domain.TestAuditEntity;
import com.raffs.LawInsight.domain.TestAuditEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JpaConfigTest {

    @Autowired
    private AuditorAware<String> auditorAware;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private TestAuditEntityRepository repository;

    @Test
    void shouldReturnSystemAuditorWhenNoSecurityContext() {
        var auditor = auditorAware.getCurrentAuditor();
        assertThat(auditor).contains("SYSTEM");
    }

    @Test
    void shouldProvideUtcDateTime() {
        var now = dateTimeProvider.getNow();
        assertThat(now).isPresent();
        var offsetDateTime = (OffsetDateTime) now.get();
        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    void shouldAutoPopulateCreatedByOnPersist() {
        var entity = new TestAuditEntity();
        entity.setName("jpa-config-test");
        var saved = repository.save(entity);

        assertThat(saved.getCreatedBy()).isEqualTo("SYSTEM");
        assertThat(saved.getLastModifiedBy()).isEqualTo("SYSTEM");
    }
}
