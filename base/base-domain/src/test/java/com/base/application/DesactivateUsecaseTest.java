package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DesactivateRepositoryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesactivateUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    DesactivateRepositoryPort<User> desactivateRepositoryPort;
    @InjectMocks
    DesactivateUsecase<User> usecase;

    @Test
    void execute_shouldReturnWhatRepositoryDesactivates() {
        var desactivated = new User();
        when(desactivateRepositoryPort.desactivate(1L)).thenReturn(desactivated);

        var result = usecase.execute(1L);

        assertThat(result).isSameAs(desactivated);
        verify(desactivateRepositoryPort).desactivate(1L);
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        when(desactivateRepositoryPort.desactivate(99L))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(99L))
            .isInstanceOf(MetierException.class);
    }
}