package com.auth.infrastructure.profile.adapter;

import com.auth.infrastructure.profile.mapper.ProfileMapper;
import com.auth.infrastructure.profile.persistence.repository.ProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
}