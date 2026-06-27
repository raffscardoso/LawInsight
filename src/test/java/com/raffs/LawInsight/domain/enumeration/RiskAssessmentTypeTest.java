package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskAssessmentTypeTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(RiskAssessmentType.values())
                .hasSize(4)
                .containsExactly(
                        RiskAssessmentType.CLAUSE_RISK, RiskAssessmentType.FINANCIAL_RISK,
                        RiskAssessmentType.GENERAL, RiskAssessmentType.COMPLIANCE);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(RiskAssessmentType.valueOf("CLAUSE_RISK")).isEqualTo(RiskAssessmentType.CLAUSE_RISK);
        assertThat(RiskAssessmentType.valueOf("FINANCIAL_RISK")).isEqualTo(RiskAssessmentType.FINANCIAL_RISK);
        assertThat(RiskAssessmentType.valueOf("GENERAL")).isEqualTo(RiskAssessmentType.GENERAL);
        assertThat(RiskAssessmentType.valueOf("COMPLIANCE")).isEqualTo(RiskAssessmentType.COMPLIANCE);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> RiskAssessmentType.valueOf("INVALID"));
    }
}
