package com.auth.infrastructure.profile.adapter;

import java.util.Optional;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.mapper.ProfileMapper;
import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.auth.infrastructure.profile.persistence.repository.ProfileRepository;
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
class ProfileAdapterTest {

    @Mock
    private ProfileRepository repository;

    @Mock
    private ProfileMapper mapper;

    private ProfileAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProfileAdapter(repository, mapper);
    }

    @Test
    void isNotNull() {
        assertThat(adapter).isNotNull();
    }

    @Test
    void getProfileUser_returnsMappedProfile_whenFound() {
        var entity = new ProfileEntity();
        var domain = new Profile();
        when(repository.findByName("USER")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.getProfileUser();

        assertThat(result).isSameAs(domain);
        verify(repository).findByName("USER");
        verify(mapper).toDomain(entity);
    }

    @Test
    void getProfileUser_throwsMetierException_whenNotFound() {
        when(repository.findByName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.getProfileUser())
            .isInstanceOf(MetierException.class)
            .hasMessage("PROFILE USER not found");

        verifyNoInteractions(mapper);
    }
}