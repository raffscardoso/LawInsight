package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.dto.UserRequest;
import com.raffs.LawInsight.dto.UserResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.UserMapper;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User createUser() {
        var user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("attorney@law.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.ATTORNEY);
        return user;
    }

    @Test
    void shouldCreate() {
        var request = new UserRequest();
        request.setEmail("new@law.com");
        request.setPassword("secret");
        request.setFirstName("Jane");
        request.setLastName("Dao");
        request.setRole(UserRole.ATTORNEY);

        var saved = createUser();
        when(userRepository.existsByEmail("new@law.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(saved);
        when(passwordEncoder.encode("secret")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(saved)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(new UserResponse());

        var result = userService.create(request);
        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("secret");
        assertThat(saved.getPassword()).isEqualTo("$2a$10$encodedHash");
    }

    @Test
    void shouldThrowWhenEmailExists() {
        var request = new UserRequest();
        request.setEmail("existing@law.com");

        when(userRepository.existsByEmail("existing@law.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void shouldFindById() {
        var user = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse());

        var result = userService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        var user = createUser();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse());

        var result = userService.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindByRole() {
        var user = createUser();
        when(userRepository.findByRole(UserRole.ATTORNEY)).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse());

        var result = userService.findByRole(UserRole.ATTORNEY);
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
