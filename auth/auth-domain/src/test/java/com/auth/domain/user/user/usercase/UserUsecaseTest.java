package com.auth.domain.user.user.usercase;

import com.auth.domain.user.user.port.out.IUserAdapterPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserUsecaseTest {

    @Mock
    private IUserAdapterPort port;

    private UserUsecase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UserUsecase(port);
    }

    @Test
    void ping_retournePing() {
        assertThat(useCase.ping()).isEqualTo("ping");
    }
}
