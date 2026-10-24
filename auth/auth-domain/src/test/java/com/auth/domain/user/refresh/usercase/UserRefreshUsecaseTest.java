package com.auth.domain.user.refresh.usercase;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.out.IUserRefreshAdapterPort;
import com.auth.domain.user.user.model.User;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRefreshUsecaseTest {

    @Mock
    private IUserRefreshAdapterPort adapter;

    @Mock
    private User user;

    @InjectMocks
    private UserRefreshUsecase usecase;

    @Captor
    private ArgumentCaptor<UserRefresh> captor;

    @Test
    void generateUserRefresh_createsRefreshAndReturnsToken() {
        var before = Instant.now();

        var token = usecase.generateUserRefresh(user, 3600);

        var after = Instant.now();
        verify(adapter).create(captor.capture());
        var saved = captor.getValue();

        assertThat(token).matches("^[A-Za-z0-9_-]{43}$"); // SHA-256 en base64url
        assertThat(saved.getToken()).isEqualTo(token);
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getIpAddress()).isEmpty();
        assertThat(saved.getRevoked()).isFalse();
        assertThat(saved.getExpiredDate())
            .isBetween(before.plusSeconds(3600), after.plusSeconds(3600));
    }

    @Test
    void generateUserRefresh_generatesDifferentTokensEachTime() {
        var first = usecase.generateUserRefresh(user, 60);
        var second = usecase.generateUserRefresh(user, 60);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void findByToken_returnsRefreshWhenFound() {
        var refresh = new UserRefresh();
        when(adapter.findByToken("abc")).thenReturn(refresh);

        assertThat(usecase.findByToken("abc")).isSameAs(refresh);
    }

    @Test
    void findByToken_throwsWhenNotFound() {
        when(adapter.findByToken("unknown")).thenReturn(null);

        assertThatThrownBy(() -> usecase.findByToken("unknown"))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(UserRefreshUsecase.ERROR_REFRESH_TOKEN);
    }

    @Test
    void deleteByToken_delegatesToAdapter() {
        when(adapter.deleteUserRefreshByToken("abc")).thenReturn(1L);

        assertThat(usecase.deleteByToken("abc")).isEqualTo(1L);
        verify(adapter).deleteUserRefreshByToken("abc");
    }

    @Test
    void deleteExpiredTokens_delegatesToAdapter() {
        when(adapter.deleteUserRefreshWhenExpired()).thenReturn(5L);

        assertThat(usecase.deleteExpiredTokens()).isEqualTo(5L);
        verify(adapter).deleteUserRefreshWhenExpired();
    }
}