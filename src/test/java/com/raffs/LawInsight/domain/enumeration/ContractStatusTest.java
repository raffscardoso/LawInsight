package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContractStatusTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(ContractStatus.values())
                .hasSize(4)
                .containsExactly(ContractStatus.UPLOADED, ContractStatus.PROCESSING,
                        ContractStatus.PROCESSED, ContractStatus.FAILED);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(ContractStatus.valueOf("UPLOADED")).isEqualTo(ContractStatus.UPLOADED);
        assertThat(ContractStatus.valueOf("PROCESSING")).isEqualTo(ContractStatus.PROCESSING);
        assertThat(ContractStatus.valueOf("PROCESSED")).isEqualTo(ContractStatus.PROCESSED);
        assertThat(ContractStatus.valueOf("FAILED")).isEqualTo(ContractStatus.FAILED);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ContractStatus.valueOf("UNKNOWN"));
    }
}
