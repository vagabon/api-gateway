package com.auth.domain.profile.usercase;

import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfileUseCaseTest {

    @Mock
    private IProfileRepositoryPort port;

    private ProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProfileUseCase(port);
    }

    @Test
    void ping_retournePing() {
        assertThat(useCase.ping()).isEqualTo("ping");
    }
}
