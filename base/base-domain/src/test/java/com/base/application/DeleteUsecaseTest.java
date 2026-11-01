package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DeleteRepositoryPort;
import com.base.domain.port.FindByIdRepositoryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    FindByIdRepositoryPort<User> findByIdRepositoryPort;
    @Mock
    DeleteRepositoryPort<User> deleteRepositoryPort;

    @InjectMocks
    DeleteUsecase<User> usecase;

    @Test
    void execute_shouldReturnTrue_whenRepositoryDeletes() {
        var user = new User();
        user.setId(1L);
        when(findByIdRepositoryPort.findById(1L)).thenReturn(user);

        usecase.execute(1L);

        verify(deleteRepositoryPort).delete(user);
    }
}