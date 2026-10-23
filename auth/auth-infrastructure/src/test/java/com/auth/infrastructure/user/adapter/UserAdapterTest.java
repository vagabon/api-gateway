package com.auth.infrastructure.user.adapter;

import com.auth.infrastructure.user.mapper.UserMapper;
import com.auth.infrastructure.user.persistence.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    private UserAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserAdapter(repository, mapper);
    }

    @Test
    void isNotNull() {
        assertThat(adapter).isNotNull();
    }
}