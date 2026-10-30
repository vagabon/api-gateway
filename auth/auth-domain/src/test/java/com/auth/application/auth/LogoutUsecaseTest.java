package com.auth.application.auth;

import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutUsecaseTest {

    @Mock
    AuthCookieRepositoryPort authCookieRepository;
    @Mock
    DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;
    @InjectMocks
    LogoutUsecase usecase;

    @Test
    void execute_shouldDeleteRefreshTokenThenReturnClearedCookie() {
        var clearedCookie = new Object();
        when(authCookieRepository.clear()).thenReturn(clearedCookie);

        var result = usecase.execute("refresh-1");

        assertThat(result).isSameAs(clearedCookie);
        var order = inOrder(deleteUserRefreshByTokenUsecase, authCookieRepository);
        order.verify(deleteUserRefreshByTokenUsecase).execute("refresh-1");
        order.verify(authCookieRepository).clear();
    }

    @Test
    void execute_shouldPropagateExceptionAndNotClearCookie_whenDeleteFails() {
        org.mockito.Mockito.doThrow(new MetierException("ERROR"))
            .when(deleteUserRefreshByTokenUsecase).execute("refresh-1");

        assertThatThrownBy(() -> usecase.execute("refresh-1"))
            .isInstanceOf(MetierException.class);

        verify(authCookieRepository, never()).clear();
    }

    @Test
    void execute_shouldStillClearCookie_whenTokenIsNull() {
        var clearedCookie = new Object();
        when(authCookieRepository.clear()).thenReturn(clearedCookie);

        var result = usecase.execute(null);

        assertThat(result).isSameAs(clearedCookie);
        verify(deleteUserRefreshByTokenUsecase).execute(any());
    }
}