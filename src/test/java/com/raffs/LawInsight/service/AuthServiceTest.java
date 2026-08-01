package com.raffs.LawInsight.service;

import com.raffs.LawInsight.config.JwtProperties;
import com.raffs.LawInsight.dto.AuthResponse;
import com.raffs.LawInsight.dto.LoginRequest;
import com.raffs.LawInsight.dto.RefreshTokenRequest;
import com.raffs.LawInsight.security.CustomUserDetailsService;
import com.raffs.LawInsight.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("attorney@law.com", "secret", List.of(new SimpleGrantedAuthority("ROLE_ATTORNEY")));
    }

    @Test
    void shouldAuthenticateAndReturnTokensOnLogin() {
        LoginRequest loginRequest = new LoginRequest("attorney@law.com", "secret");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("attorney@law.com")).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600000L);
    }

    @Test
    void shouldThrowExceptionWhenLoginWithBadCredentials() {
        LoginRequest loginRequest = new LoginRequest("attorney@law.com", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void shouldRefreshAccessTokenWithValidRefreshToken() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-refresh-token");

        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token")).thenReturn("attorney@law.com");
        when(userDetailsService.loadUserByUsername("attorney@law.com")).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.refreshToken(refreshRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-refresh-token");

        when(jwtTokenProvider.validateToken("invalid-refresh-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(refreshRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid refresh token");
    }
}
