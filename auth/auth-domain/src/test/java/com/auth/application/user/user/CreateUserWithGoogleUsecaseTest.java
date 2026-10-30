package com.auth.application.user.user;

import java.time.Instant;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.ProfileRepositoryPort;
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
    ProfileRepositoryPort profileRepository;
    @InjectMocks
    CreateUserWithGoogleUsecase usecase;

    private final AuthGoogle google = new AuthGoogle("sub-1", "aud-1",
        "vagabond@example.com",
        true,
        "Vagabond",
        "avatar.png");

    @Test
    void execute_shouldCreateUserWithGoogleFieldsAndDefaultProfile() {
        var profile = new Profile();
        when(profileRepository.getProfileUser()).thenReturn(profile);
        when(userRepository.create(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        var before = Instant.now();

        var result = usecase.execute(google);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).create(captor.capture());
        var created = captor.getValue();
        assertThat(result).isSameAs(created);
        assertThat(created.getGoogleId()).isEqualTo("sub-1");
        assertThat(created.getUsername()).startsWith("vagabond");
        assertThat(created.getEmail()).isEqualTo("vagabond@example.com");
        assertThat(created.getAvatar()).isEqualTo("avatar.png");
        assertThat(created.getLastConnexionDate()).isBetween(before, Instant.now());
        assertThat(created.getProfiles()).containsExactly(profile);
    }

    @Test
    void execute_shouldCreateUserWithoutProfile_whenNoDefaultProfile() {
        when(profileRepository.getProfileUser()).thenReturn(null);
        when(userRepository.create(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        var result = usecase.execute(google);

        assertThat(result.getProfiles()).isNullOrEmpty();
    }

    @Test
    void execute_shouldReturnWhatRepositoryCreateReturns() {
        var persisted = new User();
        when(profileRepository.getProfileUser()).thenReturn(null);
        when(userRepository.create(any(User.class))).thenReturn(persisted);

        var result = usecase.execute(google);

        assertThat(result).isSameAs(persisted);
    }
}