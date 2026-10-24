package com.auth.infrastructure.auth.service;

import java.time.Instant;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.auth.dto.response.google.AuthGoogleResponse;
import com.auth.infrastructure.profile.adapter.ProfileAdapter;
import com.auth.infrastructure.user.user.adapter.UserAdapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private ProfileAdapter profileAdapter;

    @InjectMocks
    private AuthService service;

    private final AuthGoogleResponse google = new AuthGoogleResponse("sub-1",
        "gonz@mail.com", "Gonz", "http://pic");

    @Test
    void googleConnect_existingUser_updatesAvatarAndLastConnexion() {
        var existing = new User();
        existing.setGoogleId("sub-1");
        existing.setAvatar("http://old");
        when(userAdapter.findByGoogleId("sub-1")).thenReturn(existing);
        var before = Instant.now();

        var result = service.googleConnect(google);

        assertThat(result).isSameAs(existing);
        assertThat(result.getAvatar()).isEqualTo("http://pic");
        assertThat(result.getLastConnexionDate())
            .isBetween(before, Instant.now());
        verify(userAdapter, never()).create(any());
        verifyNoInteractions(profileAdapter);
    }

    @Test
    void googleConnect_newUser_createsUserWithDefaultProfile() {
        var profile = new Profile();
        when(userAdapter.findByGoogleId("sub-1")).thenReturn(null);
        when(profileAdapter.getProfileUser()).thenReturn(profile);
        when(userAdapter.create(any(User.class))).thenAnswer(i -> i.getArgument(0));
        var before = Instant.now();

        var result = service.googleConnect(google);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userAdapter).create(captor.capture());
        var created = captor.getValue();

        assertThat(result).isSameAs(created);
        assertThat(created.getGoogleId()).isEqualTo("sub-1");
        assertThat(created.getEmail()).isEqualTo("gonz@mail.com");
        assertThat(created.getAvatar()).isEqualTo("http://pic");
        assertThat(created.getUsername()).isNotBlank();
        assertThat(created.getLastConnexionDate()).isBetween(before, Instant.now());
        assertThat(created.getProfiles()).containsExactly(profile);
    }

    @Test
    void googleConnect_newUser_withoutDefaultProfile_createsUserWithoutProfiles() {
        when(userAdapter.findByGoogleId("sub-1")).thenReturn(null);
        when(profileAdapter.getProfileUser()).thenReturn(null);
        when(userAdapter.create(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.googleConnect(google);

        assertThat(result.getProfiles()).isNullOrEmpty();
        verify(userAdapter).create(result);
    }

    @Test
    void googleConnect_newUser_profilesListIsMutable() {
        var profile = new Profile();
        when(userAdapter.findByGoogleId("sub-1")).thenReturn(null);
        when(profileAdapter.getProfileUser()).thenReturn(profile);
        when(userAdapter.create(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.googleConnect(google);

        assertThatCode(() -> result.getProfiles().add(new Profile()))
            .doesNotThrowAnyException();
    }
}