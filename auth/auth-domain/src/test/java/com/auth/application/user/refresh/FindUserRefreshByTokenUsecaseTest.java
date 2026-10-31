package com.auth.application.user.refresh;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.base.domain.exception.MetierException;

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
class FindUserRefreshByTokenUsecaseTest {

    @Mock
    UserRefreshRepositoryPort userRefreshRepository;
    @InjectMocks
    FindUserRefreshByTokenUsecase usecase;

    private final String origin = "http://localhost:4200";

    @Test
    void execute_shouldReturnUserRefresh_whenTokenExists() {
        var expected = new UserRefresh();
        expected.setExpiredDate(Instant.now().plusSeconds(10000000));
        when(userRefreshRepository.findByTokenAndOrigin("refresh-1", origin))
            .thenReturn(expected);

        var result = usecase.execute("refresh-1", origin);

        assertThat(result).isSameAs(expected);
        verify(userRefreshRepository).findByTokenAndOrigin("refresh-1", origin);
    }

    @Test
    void execute_shouldThrowMetierException_whenTokenRevoked() {
        var expected = new UserRefresh();
        expected.setRevoked(true);
        when(userRefreshRepository.findByTokenAndOrigin("refresh-1", origin))
            .thenReturn(expected);

        assertThatThrownBy(() -> usecase.execute("refresh-1", origin))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(FindUserRefreshByTokenUsecase.USER_REFRESH_REVOKED);
    }

    @Test
    void execute_shouldThrowMetierException_whenTokenExpired() {
        var expected = new UserRefresh();
        expected.setExpiredDate(Instant.now().minusSeconds(10000000));
        when(userRefreshRepository.findByTokenAndOrigin("refresh-1", origin))
            .thenReturn(expected);

        assertThatThrownBy(() -> usecase.execute("refresh-1", origin))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(FindUserRefreshByTokenUsecase.USER_REFRESH_EXPIRED);
    }

    @Test
    void execute_shouldThrowMetierException_whenTokenUnknown() {
        when(userRefreshRepository.findByTokenAndOrigin("unknown", origin))
            .thenReturn(null);

        assertThatThrownBy(() -> usecase.execute("unknown", origin))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(FindUserRefreshByTokenUsecase.USER_REFRESH_NOT_FOUND);
    }
}