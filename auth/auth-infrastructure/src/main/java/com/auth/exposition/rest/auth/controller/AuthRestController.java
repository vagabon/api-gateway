package com.auth.exposition.rest.auth.controller;

import jakarta.servlet.http.HttpServletRequest;

import com.auth.application.auth.AuthenticateRefreshUsecase;
import com.auth.application.auth.AuthenticateUsecase;
import com.auth.application.auth.GoogleConnectUsecase;
import com.auth.application.auth.LogoutUsecase;
import com.auth.exposition.rest.auth.request.AuthGoogleRequest;
import com.auth.exposition.rest.auth.response.AuthJwtResponse;
import com.base.infrastructure.controller.BaseController;
import com.google.common.net.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthRestController extends BaseController {

    private final GoogleConnectUsecase googleConnectUsecase;
    private final AuthenticateUsecase authenticateUsecase;
    private final AuthenticateRefreshUsecase authenticateRefreshUsecase;
    private final LogoutUsecase logoutUsecase;

    @PostMapping("/google-identity-connect")
    public ResponseEntity<AuthJwtResponse> googleConnectIdentity(
        @RequestBody AuthGoogleRequest googleRequest) {

        var googleResponse = googleConnectUsecase.execute(googleRequest.googleToken());

        var authIdentity = authenticateUsecase.execute(googleResponse);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authIdentity.cookie().toString())
            .body(new AuthJwtResponse(authIdentity.user(), authIdentity.jwt()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthJwtResponse> refreshToken(
        @CookieValue(name = "${app.refresh-cookie.name}", required = false) String refreshToken,
        HttpServletRequest request) {

        var authIdentity = authenticateRefreshUsecase.execute(refreshToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authIdentity.cookie().toString())
            .body(new AuthJwtResponse(authIdentity.user(), authIdentity.jwt()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue(name = "${app.refresh-cookie.name}", required = true) String refreshToken) {

        var cookie = logoutUsecase.execute(refreshToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}