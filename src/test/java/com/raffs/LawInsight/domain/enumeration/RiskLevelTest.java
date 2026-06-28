package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskLevelTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(RiskLevel.values())
                .hasSize(4)
                .containsExactly(RiskLevel.LOW, RiskLevel.MEDIUM,
                        RiskLevel.HIGH, RiskLevel.CRITICAL);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(RiskLevel.valueOf("LOW")).isEqualTo(RiskLevel.LOW);
        assertThat(RiskLevel.valueOf("MEDIUM")).isEqualTo(RiskLevel.MEDIUM);
        assertThat(RiskLevel.valueOf("HIGH")).isEqualTo(RiskLevel.HIGH);
        assertThat(RiskLevel.valueOf("CRITICAL")).isEqualTo(RiskLevel.CRITICAL);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> RiskLevel.valueOf("UNKNOWN"));
    }

    @Test
    void shouldHaveSeverityValuesInOrder() {
        assertThat(RiskLevel.LOW.getSeverity()).isEqualTo(1);
        assertThat(RiskLevel.MEDIUM.getSeverity()).isEqualTo(2);
        assertThat(RiskLevel.HIGH.getSeverity()).isEqualTo(3);
        assertThat(RiskLevel.CRITICAL.getSeverity()).isEqualTo(4);
    }

    @Test
    void shouldCompareSeverityCorrectly() {
        assertThat(RiskLevel.HIGH.getSeverity()).isGreaterThan(RiskLevel.LOW.getSeverity());
        assertThat(RiskLevel.CRITICAL.getSeverity()).isGreaterThan(RiskLevel.MEDIUM.getSeverity());
        assertThat(RiskLevel.MEDIUM.getSeverity()).isGreaterThan(RiskLevel.LOW.getSeverity());
    }
}
