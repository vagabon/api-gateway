package com.auth.infrastructure.auth.controller;

import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public static final String REFRESH_TOKEN_ERROR = "NO_REFRESH_TOKEN";

    private final AuthGoogleClient authClient;
    private final AuthCookieConfig authCookieConfig;
    private final AuthService authService;
    private final AuthJwtService authJwtService;
    private final UserRefreshService userRefreshService;
    private final String googleClientId;

    public AuthController(
        AuthGoogleClient authClient,
        AuthCookieConfig authCookieConfig,
        AuthService authService,
        UserRefreshService userRefreshService,
        AuthJwtService authJwtService,
        @Value("${google.client-id}") String googleClientId) {

        this.authClient = authClient;
        this.authCookieConfig = authCookieConfig;
        this.authService = authService;
        this.userRefreshService = userRefreshService;
        this.authJwtService = authJwtService;
        this.googleClientId = googleClientId;
    }

    @PostMapping("/google-identity-connect")
    public ResponseEntity<AuthJwtResponse> googleConnectIdentity(
        @RequestBody AuthGoogleRequest googleRequest) {

        var identity = getGoogleIdentity(googleRequest);

        if (identity == null || !googleClientId.equals(identity.aud())
            || !identity.emailVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Invalid Google token");
        }

        var googleResponse = new AuthGoogleResponse(
            identity.sub(), identity.email(), identity.givenName(), identity.picture());
        var user = authService.googleConnect(googleResponse);
        var useJwt = authJwtService.getJwtTokens(user);

        var refreshToken = userRefreshService.generateUserRefresh(user);

        var cookie = authCookieConfig.create(
            refreshToken, Duration.ofSeconds(userRefreshService.getRefreshTokenTtl()));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(useJwt);
    }

    private AuthGoogleHttpResponse getGoogleIdentity(AuthGoogleRequest googleRequest) {
        try {
            return authClient.verifyIdToken(googleRequest.googleToken());
        } catch (RestClientResponseException _) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Invalid Google token");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthJwtResponse> refreshToken(
        @CookieValue(name = "${app.refresh-cookie.name}", required = false) String refreshToken,
        HttpServletRequest request) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new MetierException(REFRESH_TOKEN_ERROR);
        }
        var user = userRefreshService.findUserByRefreshToken(refreshToken);
        if (user == null) {
            throw new MetierException(REFRESH_TOKEN_ERROR);
        }
        userRefreshService.deleteUserRefresh(refreshToken);

        var useJwt = authJwtService.getJwtTokens(user);

        var newRefreshToken = userRefreshService.generateUserRefresh(user);

        var cookie = authCookieConfig.create(
            newRefreshToken, Duration.ofSeconds(userRefreshService.getRefreshTokenTtl()));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(useJwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var cookie = authCookieConfig.create(
            "", Duration.ofSeconds(0));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}