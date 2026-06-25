package com.raffs.LawInsight.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAuditEntityRepository extends JpaRepository<TestAuditEntity, Long> {
}
