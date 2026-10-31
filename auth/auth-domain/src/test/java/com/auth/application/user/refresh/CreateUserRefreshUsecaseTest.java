package com.auth.application.user.refresh;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.auth.domain.user.user.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateUserRefreshUsecaseTest {

    private static final int TTL = 3600;

    @Mock
    UserRefreshRepositoryPort userRefreshRepository;
    @InjectMocks
    CreateUserRefreshUsecase usecase;

    private final String origin = "http://localhost:4200";
    private final String ipAdress = "127.0.0.1";

    @Test
    void execute_shouldPersistRefreshWithExpectedFields() {
        var user = new User();
        var before = Instant.now();

        var token = usecase.execute(user, origin, ipAdress, TTL);

        var captor = ArgumentCaptor.forClass(UserRefresh.class);
        verify(userRefreshRepository).create(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getToken()).isEqualTo(token);
        assertThat(saved.getOrigin()).isEqualTo(origin);
        assertThat(saved.getIpAddress()).isEqualTo(ipAdress);
        assertThat(saved.getRevoked()).isFalse();
        assertThat(saved.getExpiredDate())
            .isBetween(before.plusSeconds(TTL), Instant.now().plusSeconds(TTL));
    }

    @Test
    void execute_shouldReturnNonBlankToken() {
        var token = usecase.execute(new User(), origin, ipAdress, TTL);

        assertThat(token).isNotBlank();
    }

    @Test
    void execute_shouldGenerateDistinctTokensOnEachCall() {
        var first = usecase.execute(new User(), origin, ipAdress, TTL);
        var second = usecase.execute(new User(), origin, ipAdress, TTL);

        assertThat(first).isNotEqualTo(second);
    }
}