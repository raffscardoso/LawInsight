package com.raffs.LawInsight.domain.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void shouldHaveExpectedConstants() {
        assertThat(UserRole.values())
                .hasSize(4)
                .containsExactly(UserRole.ADMIN, UserRole.ATTORNEY,
                        UserRole.PARALEGAL, UserRole.ASSISTANT);
    }

    @Test
    void shouldResolveFromString() {
        assertThat(UserRole.valueOf("ADMIN")).isEqualTo(UserRole.ADMIN);
        assertThat(UserRole.valueOf("ATTORNEY")).isEqualTo(UserRole.ATTORNEY);
        assertThat(UserRole.valueOf("PARALEGAL")).isEqualTo(UserRole.PARALEGAL);
        assertThat(UserRole.valueOf("ASSISTANT")).isEqualTo(UserRole.ASSISTANT);
    }

    @Test
    void shouldThrowOnInvalidValue() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> UserRole.valueOf("INVALID_ROLE"));
    }
}
