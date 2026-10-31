package com.auth.application.user.user;

import java.time.Instant;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.role.model.Role;
import com.auth.domain.role.port.RoleRepositoryPort;
import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserWithGoogleUsecaseTest {

    @Mock
    UserRepositoryPort userRepository;
    @Mock
    RoleRepositoryPort roleRepository;
    @InjectMocks
    CreateUserWithGoogleUsecase usecase;

    private final AuthGoogle google = new AuthGoogle("sub-1", "aud-1",
        "vagabond@example.com",
        true,
        "Vagabond",
        "avatar.png");

    @Test
    void execute_shouldCreateUserWithGoogleFieldsAndDefaultRole() {
        var role = new Role();
        when(roleRepository.getRoleForUser("http://localhost:4200")).thenReturn(role);
        when(userRepository.create(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        var before = Instant.now();

        var result = usecase.execute(google, "http://localhost:4200");

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).create(captor.capture());
        var created = captor.getValue();
        assertThat(result).isSameAs(created);
        assertThat(created.getGoogleId()).isEqualTo("sub-1");
        assertThat(created.getUsername()).startsWith("vagabond");
        assertThat(created.getEmail()).isEqualTo("vagabond@example.com");
        assertThat(created.getAvatar()).isEqualTo("avatar.png");
        assertThat(created.getLastConnexionDate()).isBetween(before, Instant.now());
        assertThat(created.getRoles()).containsExactly(role);
    }

    @Test
    void execute_shouldCreateUserWithoutRole_whenNoDefaultRole() {
        when(roleRepository.getRoleForUser("http://localhost:4200")).thenReturn(null);
        when(userRepository.create(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        var result = usecase.execute(google, "http://localhost:4200");

        assertThat(result.getRoles()).isNullOrEmpty();
    }

    @Test
    void execute_shouldReturnWhatRepositoryCreateReturns() {
        var persisted = new User();
        when(roleRepository.getRoleForUser("http://localhost:4200")).thenReturn(null);
        when(userRepository.create(any(User.class))).thenReturn(persisted);

        var result = usecase.execute(google, "http://localhost:4200");

        assertThat(result).isSameAs(persisted);
    }
}