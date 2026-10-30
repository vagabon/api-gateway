package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.port.DeleteRepositoryPort;

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
class DeleteUsecaseTest {

    @Mock
    DeleteRepositoryPort deleteRepositoryPort;
    @InjectMocks
    DeleteUsecase usecase;

    @Test
    void execute_shouldReturnTrue_whenRepositoryDeletes() {
        when(deleteRepositoryPort.delete(1L)).thenReturn(true);

        var result = usecase.execute(1L);

        assertThat(result).isTrue();
        verify(deleteRepositoryPort).delete(1L);
    }

    @Test
    void execute_shouldReturnFalse_whenNothingToDelete() {
        when(deleteRepositoryPort.delete(99L)).thenReturn(false);

        var result = usecase.execute(99L);

        assertThat(result).isFalse();
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        when(deleteRepositoryPort.delete(1L)).thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(1L))
            .isInstanceOf(MetierException.class);
    }
}