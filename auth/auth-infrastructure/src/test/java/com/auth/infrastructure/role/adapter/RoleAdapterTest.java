package com.auth.infrastructure.role.adapter;

import java.util.Optional;

import com.auth.domain.role.model.Role;
import com.auth.infrastructure.role.jpa.entity.RoleJpaEntity;
import com.auth.infrastructure.role.jpa.repository.RoleJpaRepository;
import com.auth.infrastructure.role.mapper.RoleMapper;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAdapterTest {

    @Mock
    private RoleJpaRepository repository;

    @Mock
    private RoleMapper mapper;

    private RoleAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RoleAdapter(repository, mapper);
    }

    @Test
    void isNotNull() {
        assertThat(adapter).isNotNull();
    }

    @Test
    void getProfileUser_returnsMappedProfile_whenFound() {
        var entity = new RoleJpaEntity();
        var domain = new Role();
        when(repository.findByRoleNameAndOrigin("USER", "http://localhost:4200"))
            .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.getRoleForUser("http://localhost:4200");

        assertThat(result).isSameAs(domain);
        verify(repository).findByRoleNameAndOrigin("USER", "http://localhost:4200");
        verify(mapper).toDomain(entity);
    }

    @Test
    void getProfileUser_throwsMetierException_whenNotFound() {
        when(repository.findByRoleNameAndOrigin("USER", "http://localhost:4200"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.getRoleForUser("http://localhost:4200"))
            .isInstanceOf(MetierException.class)
            .hasMessage("ERROR.PROFILE_USER_NOT_FOUND");

        verifyNoInteractions(mapper);
    }
}