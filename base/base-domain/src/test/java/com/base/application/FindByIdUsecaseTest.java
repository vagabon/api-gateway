package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.FindByIdRepositoryPort;

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
class FindByIdUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    FindByIdRepositoryPort<User> findByIdRepositoryPort;
    @InjectMocks
    FindByIdUsecase<User> usecase;

    @Test
    void execute_shouldReturnWhatRepositoryFinds() {
        var found = new User();
        when(findByIdRepositoryPort.findById(1L)).thenReturn(found);

        var result = usecase.execute(1L);

        assertThat(result).isSameAs(found);
        verify(findByIdRepositoryPort).findById(1L);
    }

    @Test
    void execute_shouldReturnNull_whenRepositoryFindsNothing() {
        when(findByIdRepositoryPort.findById(99L)).thenReturn(null);

        assertThat(usecase.execute(99L)).isNull();
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        when(findByIdRepositoryPort.findById(99L))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(99L))
            .isInstanceOf(MetierException.class);
    }
}