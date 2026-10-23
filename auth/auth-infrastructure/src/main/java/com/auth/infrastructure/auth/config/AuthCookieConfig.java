package com.auth.infrastructure.auth.config;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieConfig {
    private static final String STRICT = "Strict";
    private static final String ENDPOINT = "/api/auth/refresh-token";
    private static final String NAME = "refresh_token";

    public ResponseCookie create(String refreshToken, Duration ttl) {
        return ResponseCookie.from(NAME, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite(STRICT)
            .path(ENDPOINT)
            .maxAge(ttl)
            .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(NAME, "")
            .httpOnly(true).secure(true).sameSite(STRICT)
            .path(ENDPOINT)
            .maxAge(0)
            .build();
    }
}
