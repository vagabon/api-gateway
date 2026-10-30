package com.auth.application.user.refresh;

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

    @Test
    void execute_shouldReturnUserRefresh_whenTokenExists() {
        var expected = new UserRefresh();
        when(userRefreshRepository.findByToken("refresh-1")).thenReturn(expected);

        var result = usecase.execute("refresh-1");

        assertThat(result).isSameAs(expected);
        verify(userRefreshRepository).findByToken("refresh-1");
    }

    @Test
    void execute_shouldThrowMetierException_whenTokenUnknown() {
        when(userRefreshRepository.findByToken("unknown")).thenReturn(null);

        assertThatThrownBy(() -> usecase.execute("unknown"))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(FindUserRefreshByTokenUsecase.USER_NOT_FOUND);
    }
}