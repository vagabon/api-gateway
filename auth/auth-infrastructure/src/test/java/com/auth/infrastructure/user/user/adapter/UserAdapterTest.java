package com.auth.infrastructure.user.user.adapter;

import java.util.Optional;

import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.user.jpa.entity.UserJpaEntity;
import com.auth.infrastructure.user.user.jpa.repository.UserJpaRepository;
import com.auth.infrastructure.user.user.mapper.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    @Mock
    private UserJpaRepository repository;

    @Mock
    private UserMapper mapper;

    private UserAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserAdapter(repository, mapper);
    }

    @Test
    void findByGoogleId_returnsMappedUser_whenFound() {
        var entity = new UserJpaEntity();
        var domain = new User();
        when(repository.findByGoogleId("g-123")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByGoogleId("g-123");

        assertThat(result).isSameAs(domain);
        verify(repository).findByGoogleId("g-123");
    }

    @Test
    void findByGoogleId_returnsNull_whenNotFound() {
        when(repository.findByGoogleId("unknown")).thenReturn(Optional.empty());

        var result = adapter.findByGoogleId("unknown");

        assertThat(result).isNull();
    }

}