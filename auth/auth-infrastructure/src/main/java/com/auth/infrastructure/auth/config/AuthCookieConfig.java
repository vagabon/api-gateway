package com.auth.infrastructure.auth.config;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieConfig {
    private static final String STRICT = "Strict";

    public ResponseCookie create(
        String refreshTokenName,
        String refreshTokenEndpoint,
        String refreshToken,
        Duration ttl) {
        return ResponseCookie.from(refreshTokenName, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite(STRICT)
            .path(refreshTokenEndpoint)
            .maxAge(ttl)
            .build();
    }

    public ResponseCookie clear(
        String refreshTokenName,
        String refreshTokenEndpoint) {
        return ResponseCookie.from(refreshTokenName, "")
            .httpOnly(true).secure(true).sameSite(STRICT)
            .path(refreshTokenEndpoint)
            .maxAge(0)
            .build();
    }
}
