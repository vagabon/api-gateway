package com.auth.infrastructure.user.refresh.service;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.usercase.UserRefreshUsecase;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.refresh.adapter.UserRefreshAdapter;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRefreshServiceTest {

    @Mock
    private UserRefreshAdapter adapter;

    @Mock
    private User user;

    private UserRefreshService service;

    @BeforeEach
    void setUp() {
        service = new UserRefreshService(adapter);
    }

    @Test
    void generateUserRefresh_createsRefreshAndReturnsToken() {
        var captor = ArgumentCaptor.forClass(UserRefresh.class);

        var token = service.generateUserRefresh(user, 3600);

        verify(adapter).create(captor.capture());
        assertThat(token).isNotBlank().isEqualTo(captor.getValue().getToken());
        assertThat(captor.getValue().getUser()).isSameAs(user);
    }

    @Test
    void findByToken_returnsRefreshWhenFound() {
        var refresh = new UserRefresh();
        when(adapter.findByToken("abc")).thenReturn(refresh);

        assertThat(service.findByToken("abc")).isSameAs(refresh);
    }

    @Test
    void findByToken_throwsWhenNotFound() {
        when(adapter.findByToken("unknown")).thenReturn(null);

        assertThatThrownBy(() -> service.findByToken("unknown"))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(UserRefreshUsecase.ERROR_REFRESH_TOKEN);
    }

    @Test
    void deleteByToken_delegates() {
        when(adapter.deleteUserRefreshByToken("abc")).thenReturn(1L);

        assertThat(service.deleteByToken("abc")).isEqualTo(1L);
        verify(adapter).deleteUserRefreshByToken("abc");
    }

    @Test
    void deleteExpiredTokens_delegates() {
        when(adapter.deleteUserRefreshWhenExpired()).thenReturn(5L);

        assertThat(service.deleteExpiredTokens()).isEqualTo(5L);
        verify(adapter).deleteUserRefreshWhenExpired();
    }
}