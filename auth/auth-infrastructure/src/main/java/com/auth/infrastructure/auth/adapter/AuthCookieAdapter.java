package com.auth.infrastructure.auth.adapter;

import com.auth.domain.auth.port.AuthCookieRepositoryPort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class AuthCookieAdapter implements AuthCookieRepositoryPort {
    private static final String STRICT = "Strict";

    private final String refreshTokenName;
    private final String refreshTokenEndpoint;

    @Getter
    private final int refreshTokenTimeToLive;

    public AuthCookieAdapter(
        @Value("${app.refresh-cookie.name}") String refreshTokenName,
        @Value("${app.refresh-cookie.endpoint}") String refreshTokenEndpoint,
        @Value("${app.refresh-cookie.ttl}") int refreshTokenTimeToLive) {
        this.refreshTokenName = refreshTokenName;
        this.refreshTokenEndpoint = refreshTokenEndpoint;
        this.refreshTokenTimeToLive = refreshTokenTimeToLive;
    }

    public ResponseCookie create(String refreshToken) {
        return ResponseCookie.from(refreshTokenName, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite(STRICT)
            .path(refreshTokenEndpoint)
            .maxAge(refreshTokenTimeToLive)
            .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(refreshTokenName, "")
            .httpOnly(true).secure(true).sameSite(STRICT)
            .path(refreshTokenEndpoint)
            .maxAge(0)
            .build();
    }
}
