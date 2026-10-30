package com.auth.application.auth;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.application.user.refresh.FindUserRefreshByTokenUsecase;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateRefreshUsecaseTest {

    private static final int TTL = 3600;

    @Mock
    UserRepositoryPort userRepository;
    @Mock
    AuthJwtRepositoryPort authJwtRepository;
    @Mock
    AuthCookieRepositoryPort authCookieRepository;
    @Mock
    FindUserRefreshByTokenUsecase findUserRefreshByTokenUsecase;
    @Mock
    DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;
    @Mock
    CreateUserRefreshUsecase createUserRefreshUsecase;

    private AuthenticateRefreshUsecase usecase;

    @BeforeEach
    void setUp() {
        usecase = new AuthenticateRefreshUsecase(
            userRepository, authJwtRepository, authCookieRepository,
            findUserRefreshByTokenUsecase, deleteUserRefreshByTokenUsecase,
            createUserRefreshUsecase);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void execute_shouldThrow_whenTokenNullOrEmpty(String token) {
        assertThatThrownBy(() -> usecase.execute(token))
            .isInstanceOf(MetierException.class);

        verifyNoInteractions(findUserRefreshByTokenUsecase,
            deleteUserRefreshByTokenUsecase,
            createUserRefreshUsecase, userRepository, authJwtRepository,
            authCookieRepository);
    }

    @Test
    void execute_shouldRotateTokenAndReturnIdentity_whenTokenValid() {
        var user = mock(User.class);
        var userRefresh = mock(UserRefresh.class);
        when(userRefresh.getUser()).thenReturn(user);
        when(findUserRefreshByTokenUsecase.execute("old")).thenReturn(userRefresh);
        when(authCookieRepository.getRefreshTokenTimeToLive()).thenReturn(TTL);
        when(createUserRefreshUsecase.execute(user, TTL)).thenReturn("new-refresh");
        when(authJwtRepository.generateTokenJwt(user)).thenReturn("jwt");
        when(authCookieRepository.create("new-refresh")).thenReturn("cookie");

        var result = usecase.execute("old");

        assertThat(result.user()).isSameAs(user);

        var order = inOrder(findUserRefreshByTokenUsecase,
            deleteUserRefreshByTokenUsecase,
            user, userRepository, createUserRefreshUsecase);
        order.verify(findUserRefreshByTokenUsecase).execute("old");
        order.verify(deleteUserRefreshByTokenUsecase).execute("old");
        order.verify(user).updateLastConnexion();
        order.verify(userRepository).update(user);
        order.verify(createUserRefreshUsecase).execute(user, TTL);
    }

    @Test
    void execute_shouldNotCreateNewToken_whenFindThrows() {
        when(findUserRefreshByTokenUsecase.execute("unknown"))
            .thenThrow(
                new MetierException(AuthenticateRefreshUsecase.ERROR_REFRESH_TOKEN));

        assertThatThrownBy(() -> usecase.execute("unknown"))
            .isInstanceOf(MetierException.class);

        verify(deleteUserRefreshByTokenUsecase, never()).execute(any());
        verify(createUserRefreshUsecase, never()).execute(any(),
            org.mockito.ArgumentMatchers.anyInt());
        verify(userRepository, never()).update(any());
    }
}