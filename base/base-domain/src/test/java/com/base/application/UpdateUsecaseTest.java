package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.UpdateRepositoryPort;

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
class UpdateUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    UpdateRepositoryPort<User> updateRepositoryPort;
    @InjectMocks
    UpdateUsecase<User> usecase;

    @Test
    void execute_shouldReturnWhatRepositoryUpdates() {
        var input = new User();
        var updated = new User();
        when(updateRepositoryPort.update(input)).thenReturn(updated);

        var result = usecase.execute(input);

        assertThat(result).isSameAs(updated);
        verify(updateRepositoryPort).update(input);
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        var input = new User();
        when(updateRepositoryPort.update(input)).thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(input))
            .isInstanceOf(MetierException.class);
    }
}