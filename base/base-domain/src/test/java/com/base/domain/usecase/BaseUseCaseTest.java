package com.base.domain.usecase;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseUseCaseTest {

    private final BaseUseCase useCase = new BaseUseCase() {
    };

    @Test
    void ping_utiliseLaConstantePING() {
        assertThat(useCase.ping()).isEqualTo(BaseUseCase.PING);
    }
}