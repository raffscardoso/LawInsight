package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.RiskAssessmentType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "risk_assessments")
public class RiskAssessment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskAssessmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(nullable = false)
    private Instant assessedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_clause_id")
    private ContractClause contractClause;
}
