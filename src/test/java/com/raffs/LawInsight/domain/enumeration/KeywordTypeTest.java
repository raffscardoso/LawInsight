package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordTypeTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(KeywordType.values())
                .hasSize(8)
                .containsExactly(
                        KeywordType.PERSON_NAME, KeywordType.COMPANY_NAME,
                        KeywordType.DATE, KeywordType.MONEY,
                        KeywordType.OBLIGATION, KeywordType.TERM,
                        KeywordType.JURISDICTION, KeywordType.OTHER);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(KeywordType.valueOf("PERSON_NAME")).isEqualTo(KeywordType.PERSON_NAME);
        assertThat(KeywordType.valueOf("COMPANY_NAME")).isEqualTo(KeywordType.COMPANY_NAME);
        assertThat(KeywordType.valueOf("DATE")).isEqualTo(KeywordType.DATE);
        assertThat(KeywordType.valueOf("MONEY")).isEqualTo(KeywordType.MONEY);
        assertThat(KeywordType.valueOf("OBLIGATION")).isEqualTo(KeywordType.OBLIGATION);
        assertThat(KeywordType.valueOf("TERM")).isEqualTo(KeywordType.TERM);
        assertThat(KeywordType.valueOf("JURISDICTION")).isEqualTo(KeywordType.JURISDICTION);
        assertThat(KeywordType.valueOf("OTHER")).isEqualTo(KeywordType.OTHER);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> KeywordType.valueOf("INVALID"));
    }
}
