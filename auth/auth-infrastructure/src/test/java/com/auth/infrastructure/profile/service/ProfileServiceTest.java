package com.auth.infrastructure.profile.service;

import com.auth.infrastructure.profile.adapter.ProfileAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileServiceTest {

    @Mock
    private ProfileAdapter profileAdapter;

    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(profileAdapter);
    }

    @Test
    void isNotNull() {
        assertThat(profileService).isNotNull();
    }
}
