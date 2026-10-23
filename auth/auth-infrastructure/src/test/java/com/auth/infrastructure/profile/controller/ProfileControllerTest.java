package com.auth.infrastructure.profile.controller;

import com.auth.infrastructure.profile.adapter.ProfileAdapter;
import com.auth.infrastructure.profile.service.ProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private ProfileAdapter profileAdapter;

    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        profileController = new ProfileController(profileService);
    }

    @Test
    void isNotNull() {
        assertThat(profileController).isNotNull();
    }

}
