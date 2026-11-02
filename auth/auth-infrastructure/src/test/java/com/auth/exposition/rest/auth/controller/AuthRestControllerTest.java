package com.auth.exposition.rest.auth.controller;

import jakarta.servlet.http.HttpServletRequest;

import com.auth.application.auth.AuthenticateRefreshUsecase;
import com.auth.application.auth.AuthenticateUsecase;
import com.auth.application.auth.GoogleConnectUsecase;
import com.auth.application.auth.LogoutUsecase;
import com.auth.application.user.user.CachedUserUsecase;
import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.model.AuthIdentity;
import com.auth.domain.user.user.model.User;
import com.auth.exposition.rest.auth.request.AuthGoogleRequest;
import com.auth.exposition.rest.auth.response.AuthJwtResponse;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    @Mock
    GoogleConnectUsecase googleConnectUsecase;
    @Mock
    AuthenticateUsecase authenticateUsecase;
    @Mock
    AuthenticateRefreshUsecase authenticateRefreshUsecase;
    @Mock
    CachedUserUsecase cachedUserUsecase;
    @Mock
    LogoutUsecase logoutUsecase;
    @InjectMocks
    AuthRestController controller;

    private final String origin = "http://localhost:4200";
    private final String ipAdress = "127.0.0.1";

    private final User user = new User();
    private final ResponseCookie cookie = ResponseCookie
        .from("refresh_token", "refresh-1").path("/v1/auth").build();

    private AuthIdentity identity() {
        return new AuthIdentity(user, "jwt-value", cookie, "refresh-1");
    }

    // ---------- google-identity-connect ----------

    @Test
    void googleConnectIdentity_shouldReturnJwtBodyAndSetCookie() {
        var google = new AuthGoogle("sub-1", "client-id", "vagabond@example.com", true,
            "Vagabond", "pic.png");
        var request = new AuthGoogleRequest("google-token");
        var servletRequest = mock(HttpServletRequest.class);
        when(googleConnectUsecase.execute("google-token")).thenReturn(google);
        when(servletRequest.getRemoteAddr()).thenReturn(ipAdress);
        when(servletRequest.getHeader(HttpHeaders.ORIGIN)).thenReturn(origin);
        when(authenticateUsecase.execute(google, origin, ipAdress))
            .thenReturn(identity());

        var response = controller.googleConnectIdentity(request, servletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .isEqualTo(cookie.toString());
        assertThat(response.getBody()).isEqualTo(new AuthJwtResponse(user, "jwt-value"));
        var order = inOrder(googleConnectUsecase, authenticateUsecase);
        order.verify(googleConnectUsecase).execute("google-token");
        order.verify(authenticateUsecase).execute(google, origin,
            ipAdress);
    }

    @Test
    void googleConnectIdentity_shouldPropagateExceptionAndNotAuthenticate_whenGoogleRejectsToken() {
        var request = new AuthGoogleRequest("bad");
        var servletRequest = mock(HttpServletRequest.class);
        when(googleConnectUsecase.execute("bad"))
            .thenThrow(new MetierException("INVALID_GOOGLE_TOKEN"));

        assertThatThrownBy(
            () -> controller.googleConnectIdentity(request, servletRequest))
                .isInstanceOf(MetierException.class);

        verifyNoInteractions(authenticateUsecase);
    }

    // ---------- refresh-token ----------

    @Test
    void refreshToken_shouldReturnNewJwtAndRotatedCookie() {
        var servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getRemoteAddr()).thenReturn(ipAdress);
        when(servletRequest.getHeader(HttpHeaders.ORIGIN)).thenReturn(origin);
        when(authenticateRefreshUsecase.execute("old", origin, ipAdress))
            .thenReturn(identity());

        var response = controller.refreshToken("old", servletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .isEqualTo(cookie.toString());
        assertThat(response.getBody()).isEqualTo(new AuthJwtResponse(user, "jwt-value"));
    }

    @Test
    void refreshToken_shouldPropagateException_whenUsecaseRejectsToken() {
        var servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getRemoteAddr()).thenReturn(ipAdress);
        when(servletRequest.getHeader(HttpHeaders.ORIGIN)).thenReturn(origin);
        when(authenticateRefreshUsecase.execute(null, origin, ipAdress))
            .thenThrow(
                new MetierException(AuthenticateRefreshUsecase.ERROR_REFRESH_TOKEN));

        assertThatThrownBy(() -> controller.refreshToken(null, servletRequest))
            .isInstanceOf(MetierException.class);
    }

    // ---------- logout ----------

    @Test
    void logout_shouldReturnOkWithClearedCookie() {
        var cleared = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(logoutUsecase.execute("refresh-1")).thenReturn(cleared);

        var response = controller.logout("refresh-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
            .isEqualTo(cleared.toString());
        assertThat(response.getBody()).isNull();
        verify(logoutUsecase).execute("refresh-1");
    }
}