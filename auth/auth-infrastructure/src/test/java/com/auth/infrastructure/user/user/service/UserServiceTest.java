package com.auth.infrastructure.user.user.service;

import com.auth.infrastructure.user.user.adapter.UserAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {

    @Mock
    private UserAdapter userAdapter;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userAdapter);
    }

    @Test
    void isNotNull() {
        assertThat(userService).isNotNull();
    }
}
