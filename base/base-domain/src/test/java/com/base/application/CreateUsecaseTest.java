package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.CreateRepositoryPort;

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
class CreateUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    CreateRepositoryPort<User> createRepositoryPort;
    @InjectMocks
    CreateUsecase<User> usecase;

    @Test
    void execute_shouldReturnWhatRepositoryCreates() {
        var input = new User();
        var persisted = new User();
        when(createRepositoryPort.create(input)).thenReturn(persisted);

        var result = usecase.execute(input);

        assertThat(result).isSameAs(persisted);
        verify(createRepositoryPort).create(input);
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        var input = new User();
        when(createRepositoryPort.create(input))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(input))
            .isInstanceOf(MetierException.class);
    }
}