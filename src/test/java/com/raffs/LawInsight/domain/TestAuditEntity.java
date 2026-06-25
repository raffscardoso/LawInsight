package com.raffs.LawInsight.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_audit_entity")
public class TestAuditEntity extends BaseEntity {
    private String name;
}
