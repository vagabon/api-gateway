package com.auth.application.user.refresh;

import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserRefreshByTokenUsecaseTest {

    @Mock
    UserRefreshRepositoryPort userRefreshRepository;
    @InjectMocks
    DeleteUserRefreshByTokenUsecase usecase;

    @Test
    void execute_shouldDelegateDeletionToRepository() {
        usecase.execute("refresh-1");

        verify(userRefreshRepository).deleteUserRefreshByToken("refresh-1");
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        doThrow(new MetierException("ERROR"))
            .when(userRefreshRepository).deleteUserRefreshByToken("refresh-1");

        assertThatThrownBy(() -> usecase.execute("refresh-1"))
            .isInstanceOf(MetierException.class);
    }
}