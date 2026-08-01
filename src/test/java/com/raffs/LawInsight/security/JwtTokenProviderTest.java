package com.raffs.LawInsight.security;

import com.raffs.LawInsight.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtProperties jwtProperties;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("dGhpcy1pcy1hLXZlcnktc2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dC10b2tlbi1nZW5lcmF0aW9uLXRoYXQtaXMtYXQtbGVhc3QtMjU2LWJpdHM="); // Base64 256+ bit key
        jwtProperties.setExpirationMs(3600000L); // 1 hour
        jwtProperties.setRefreshExpirationMs(86400000L); // 24 hours

        jwtTokenProvider = new JwtTokenProvider(jwtProperties);

        userDetails = new User("attorney@law.com", "password", List.of(new SimpleGrantedAuthority("ROLE_ATTORNEY")));
    }

    @Test
    void shouldGenerateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken(userDetails);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("attorney@law.com");
    }

    @Test
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        assertThat(refreshToken).isNotNull().isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(refreshToken)).isEqualTo("attorney@law.com");
    }

    @Test
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() {
        jwtProperties.setExpirationMs(-1000L); // Expired 1 sec ago
        JwtTokenProvider expiredProvider = new JwtTokenProvider(jwtProperties);

        String expiredToken = expiredProvider.generateAccessToken(userDetails);

        assertThat(expiredProvider.validateToken(expiredToken)).isFalse();
    }
}
