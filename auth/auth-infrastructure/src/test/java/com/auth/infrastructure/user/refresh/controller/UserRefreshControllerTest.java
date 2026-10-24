package com.auth.infrastructure.user.refresh.controller;

import com.auth.infrastructure.user.refresh.adapter.UserRefreshAdapter;
import com.auth.infrastructure.user.refresh.service.UserRefreshService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

class UserRefreshControllerTest {

    @Mock
    private UserRefreshService userRefreshService;

    @Mock
    private UserRefreshAdapter userRefreshAdapter;

    private UserRefreshController userRefreshController;

    @BeforeEach
    void setUp() {
        userRefreshController = new UserRefreshController(userRefreshService);
    }

    @Test
    void isNotNull() {
        assertThat(userRefreshController).isNotNull();
    }
}
