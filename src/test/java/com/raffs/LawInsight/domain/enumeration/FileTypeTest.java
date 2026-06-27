package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileTypeTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(FileType.values())
                .hasSize(3)
                .containsExactly(FileType.PDF, FileType.DOCX, FileType.TXT);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(FileType.valueOf("PDF")).isEqualTo(FileType.PDF);
        assertThat(FileType.valueOf("DOCX")).isEqualTo(FileType.DOCX);
        assertThat(FileType.valueOf("TXT")).isEqualTo(FileType.TXT);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> FileType.valueOf("XML"));
    }
}
