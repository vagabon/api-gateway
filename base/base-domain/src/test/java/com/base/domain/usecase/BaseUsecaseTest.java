package com.base.domain.usecase;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseUsecaseTest {

    private final BaseUsecase useCase = new BaseUsecase() {
    };

    @Test
    void ping_utiliseLaConstantePING() {
        assertThat(useCase.ping()).isEqualTo(BaseUsecase.PING);
    }
}