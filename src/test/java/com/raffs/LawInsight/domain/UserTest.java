package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    @Autowired
    private UserRepository repository;

    private User createUser(String email, UserRole role) {
        var user = new User();
        user.setEmail(email);
        user.setPassword("$2a$10$dummyBcryptHashForTestingPurposesOnly123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBarNumber("OAB-123456");
        user.setRole(role);
        return user;
    }

    @Test
    void shouldPersistUserWithAllFields() {
        var user = createUser("attorney@lawfirm.com", UserRole.ATTORNEY);
        var saved = repository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("attorney@lawfirm.com");
        assertThat(saved.getPassword()).startsWith("$2a$");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getBarNumber()).isEqualTo("OAB-123456");
        assertThat(saved.getRole()).isEqualTo(UserRole.ATTORNEY);
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void shouldDefaultActiveToTrue() {
        var user = createUser("active-test@lawfirm.com", UserRole.PARALEGAL);
        var saved = repository.save(user);
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void shouldRejectDuplicateEmail() {
        var user1 = createUser("duplicate@lawfirm.com", UserRole.ADMIN);
        repository.saveAndFlush(user1);

        var user2 = createUser("duplicate@lawfirm.com", UserRole.ADMIN);
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(user2));
    }

    @Test
    void shouldStoreRoleAsString() {
        var user = createUser("role-test@lawfirm.com", UserRole.ADMIN);
        var saved = repository.save(user);

        assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(saved.getRole().name()).isEqualTo("ADMIN");
    }
}
