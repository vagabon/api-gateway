package com.auth.application.auth;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.port.AuthGoogleRepositoryPort;
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
class GoogleConnectUsecaseTest {

    @Mock
    AuthGoogleRepositoryPort authGoogleRepository;
    @InjectMocks
    GoogleConnectUsecase usecase;

    @Test
    void execute_shouldReturnWhatRepositoryReturns() {
        var expected = new AuthGoogle("sub-1", "aud-1",
            "vagabond@example.com",
            true,
            "Vagabond",
            "avatar.png");
        when(authGoogleRepository.googleOauth2("id-token")).thenReturn(expected);

        var result = usecase.execute("id-token");

        assertThat(result).isSameAs(expected);
        verify(authGoogleRepository).googleOauth2("id-token");
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryRejectsToken() {
        when(authGoogleRepository.googleOauth2("bad"))
            .thenThrow(new MetierException("INVALID_GOOGLE_TOKEN"));

        assertThatThrownBy(() -> usecase.execute("bad"))
            .isInstanceOf(MetierException.class);
    }
}