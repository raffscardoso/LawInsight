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
}
