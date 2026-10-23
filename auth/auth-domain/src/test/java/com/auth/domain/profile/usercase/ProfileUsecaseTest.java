package com.auth.domain.profile.usercase;

import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfileUsecaseTest {

    @Mock
    private IProfileRepositoryPort port;

    private ProfileUsecase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProfileUsecase(port);
    }

    @Test
    void ping_retournePing() {
        assertThat(useCase.ping()).isEqualTo("ping");
    }
}
