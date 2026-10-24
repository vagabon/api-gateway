package com.auth.infrastructure.auth.controller;

import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.usercase.UserRefreshUsecase;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.auth.client.AuthGoogleClient;
import com.auth.infrastructure.auth.config.AuthCookieConfig;
import com.auth.infrastructure.auth.dto.request.AuthGoogleRequest;
import com.auth.infrastructure.auth.dto.response.google.AuthGoogleHttpResponse;
import com.auth.infrastructure.auth.dto.response.google.AuthGoogleResponse;
import com.auth.infrastructure.auth.dto.response.jwt.AuthJwtResponse;
import com.auth.infrastructure.auth.service.AuthService;
import com.auth.infrastructure.auth.service.jwt.AuthJwtService;
import com.auth.infrastructure.user.refresh.service.UserRefreshService;
import com.base.domain.exception.MetierException;
import com.google.common.net.HttpHeaders;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String CLIENT_ID = "my-client-id";
    private static final int TTL = 3600;

    @Mock
    private AuthGoogleClient authClient;
    @Mock
    private AuthCookieConfig authCookieConfig;
    @Mock
    private AuthService authService;
    @Mock
    private UserRefreshService userRefreshService;
    @Mock
    private AuthJwtService authJwtService;
    @Mock
    private User user;
    @Mock
    private AuthJwtResponse jwtResponse;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authClient, authCookieConfig, authService,
            userRefreshService, authJwtService, CLIENT_ID, TTL);
    }

    private AuthGoogleHttpResponse identity(String aud, boolean emailVerified) {
        var identity = mock(AuthGoogleHttpResponse.class);
        lenient().when(identity.aud()).thenReturn(aud);
        lenient().when(identity.emailVerified()).thenReturn(emailVerified);
        lenient().when(identity.sub()).thenReturn("sub-1");
        lenient().when(identity.email()).thenReturn("gonz@mail.com");
        lenient().when(identity.givenName()).thenReturn("Gonz");
        lenient().when(identity.picture()).thenReturn("http://pic");
        return identity;
    }

    private void stubCookie(String value, Duration ttl) {
        when(authCookieConfig.create(value, ttl))
            .thenReturn(ResponseCookie.from("refresh", value).build());
    }

    // ---------- google-identity-connect ----------

    @Test
    void googleConnect_success_returnsJwtAndRefreshCookie() {
        var identity = identity(CLIENT_ID, true);
        when(authClient.verifyIdToken("g-token")).thenReturn(identity);
        when(authService.googleConnect(any(AuthGoogleResponse.class))).thenReturn(user);
        when(authJwtService.getJwtTokens(user)).thenReturn(jwtResponse);
        when(userRefreshService.generateUserRefresh(user, TTL)).thenReturn("refresh-1");
        stubCookie("refresh-1", Duration.ofSeconds(TTL));

        var response = controller.googleConnectIdentity(new AuthGoogleRequest("g-token"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(jwtResponse);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .contains("refresh=refresh-1");

        var captor = ArgumentCaptor.forClass(AuthGoogleResponse.class);
        verify(authService).googleConnect(captor.capture());
        assertThat(captor.getValue())
            .isEqualTo(
                new AuthGoogleResponse("sub-1", "gonz@mail.com", "Gonz", "http://pic"));
    }

    @Test
    void googleConnect_nullIdentity_throwsUnauthorized() {
        when(authClient.verifyIdToken("g-token")).thenReturn(null);

        assertUnauthorized(
            () -> controller.googleConnectIdentity(new AuthGoogleRequest("g-token")));
    }

    @Test
    void googleConnect_wrongAudience_throwsUnauthorized() {
        var identity = identity("other-client", true);
        when(authClient.verifyIdToken("g-token"))
            .thenReturn(identity);

        assertUnauthorized(
            () -> controller.googleConnectIdentity(new AuthGoogleRequest("g-token")));
    }

    @Test
    void googleConnect_emailNotVerified_throwsUnauthorized() {
        var identity = identity(CLIENT_ID, false);
        when(authClient.verifyIdToken("g-token")).thenReturn(identity);

        assertUnauthorized(
            () -> controller.googleConnectIdentity(new AuthGoogleRequest("g-token")));
    }

    @Test
    void googleConnect_googleClientError_throwsUnauthorized() {
        when(authClient.verifyIdToken("g-token"))
            .thenThrow(new RestClientResponseException("bad", 400, "Bad Request", null,
                null, null));

        assertUnauthorized(
            () -> controller.googleConnectIdentity(new AuthGoogleRequest("g-token")));
        verifyNoInteractions(authService, userRefreshService);
    }

    // ---------- refresh-token ----------

    @Test
    void refreshToken_success_rotatesRefreshToken() {
        var refresh = mock(UserRefresh.class);
        when(refresh.getUser()).thenReturn(user);
        when(userRefreshService.findByToken("old")).thenReturn(refresh);
        when(authJwtService.getJwtTokens(user)).thenReturn(jwtResponse);
        when(userRefreshService.generateUserRefresh(user, TTL)).thenReturn("new");
        stubCookie("new", Duration.ofSeconds(TTL));

        var response = controller.refreshToken("old", mock(HttpServletRequest.class));

        assertThat(response.getBody()).isSameAs(jwtResponse);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .contains("refresh=new");

        var order = inOrder(userRefreshService);
        order.verify(userRefreshService).deleteByToken("old");
        order.verify(userRefreshService).generateUserRefresh(user, TTL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void refreshToken_missingCookie_throwsMetierException(String token) {
        assertThatThrownBy(() -> controller.refreshToken(token, null))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(UserRefreshUsecase.ERROR_REFRESH_TOKEN);
        verifyNoInteractions(userRefreshService);
    }

    @Test
    void refreshToken_unknownToken_throwsMetierException() {
        when(userRefreshService.findByToken("unknown")).thenReturn(null);

        assertThatThrownBy(() -> controller.refreshToken("unknown", null))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining(UserRefreshUsecase.ERROR_REFRESH_TOKEN);
        verify(userRefreshService, never()).deleteByToken(any());
    }

    // ---------- logout ----------

    @Test
    void logout_returnsExpiredCookie() {
        stubCookie("", Duration.ZERO);

        var response = controller.logout();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .contains("refresh=");
        verify(authCookieConfig).create("", Duration.ofSeconds(0));
    }

    private static void assertUnauthorized(ThrowingCallable call) {
        assertThatThrownBy(call)
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}