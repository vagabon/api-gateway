package com.auth.infrastructure.user.user.adapter;

import java.time.Instant;
import java.util.Optional;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;
import com.auth.infrastructure.user.user.mapper.UserMapper;
import com.auth.infrastructure.user.user.mongo.document.UserMongoDocument;
import com.auth.infrastructure.user.user.mongo.repository.UserMongoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMongoAdapterTest {

    @Mock
    private UserMongoRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserMongoAdapter adapter;

    @Test
    void findById_shouldDelegateToRepository() {
        var doc = new UserMongoDocument();
        when(repository.findById(1L)).thenReturn(Optional.of(doc));

        var result = adapter.findById(1L);

        assertThat(result).containsSame(doc);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThat(adapter.findById(1L)).isEmpty();
    }

    @Test
    void save_shouldCreateDocumentWithAllFields_whenUserDoesNotExist() {
        var creationDate = Instant.now();
        var lastConnexion = Instant.now();

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("gonz");
        when(user.getEmail()).thenReturn("gonz@mail.com");
        when(user.getCreationDate()).thenReturn(creationDate);
        when(user.getAvatar()).thenReturn("avatar.png");
        when(user.getLastConnexionDate()).thenReturn(lastConnexion);

        var light = mock(UserLight.class);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.save(any(UserMongoDocument.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDomainLight(any(UserMongoDocument.class))).thenReturn(light);

        var result = adapter.save(user);

        var captor = ArgumentCaptor.forClass(UserMongoDocument.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUsername()).isEqualTo("gonz");
        assertThat(saved.getEmail()).isEqualTo("gonz@mail.com");
        assertThat(saved.getCreationDate()).isEqualTo(creationDate);
        assertThat(saved.getAvatar()).isEqualTo("avatar.png");
        assertThat(saved.getLastConnexionDate()).isEqualTo(lastConnexion);
        assertThat(result).isSameAs(light);
    }

    @Test
    void save_shouldOnlyUpdateAvatarAndLastConnexion_whenUserAlreadyExists() {
        var oldCreationDate = Instant.now();
        var lastConnexion = Instant.now();

        var existing = new UserMongoDocument();
        existing.setId(1L);
        existing.setUsername("old-username");
        existing.setEmail("old@mail.com");
        existing.setCreationDate(oldCreationDate);

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getAvatar()).thenReturn("new-avatar.png");
        when(user.getLastConnexionDate()).thenReturn(lastConnexion);

        var light = mock(UserLight.class);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDomainLight(existing)).thenReturn(light);

        var result = adapter.save(user);

        assertThat(existing.getUsername()).isEqualTo("old-username");
        assertThat(existing.getEmail()).isEqualTo("old@mail.com");
        assertThat(existing.getCreationDate()).isEqualTo(oldCreationDate);

        assertThat(existing.getAvatar()).isEqualTo("new-avatar.png");
        assertThat(existing.getLastConnexionDate()).isEqualTo(lastConnexion);
        assertThat(result).isSameAs(light);

        verify(user, never()).getUsername();
        verify(user, never()).getEmail();
    }

    @Test
    void evict_shouldDeleteById() {
        adapter.evict(1L);

        verify(repository).deleteById(1L);
    }
}