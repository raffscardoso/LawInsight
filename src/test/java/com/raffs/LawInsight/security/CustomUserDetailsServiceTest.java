package com.raffs.LawInsight.security;

import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setEmail("attorney@lawfirm.com");
        sampleUser.setPassword("$2a$10$encodedPasswordHash");
        sampleUser.setFirstName("Jane");
        sampleUser.setLastName("Doe");
        sampleUser.setRole(UserRole.ATTORNEY);
        sampleUser.setActive(true);
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(userRepository.findByEmail("attorney@lawfirm.com")).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("attorney@lawfirm.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("attorney@lawfirm.com");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPasswordHash");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ATTORNEY");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@lawfirm.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent@lawfirm.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: nonexistent@lawfirm.com");
    }
}
