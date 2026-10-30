package com.auth.infrastructure.auth.adapter;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCookieAdapterTest {

    private static final String NAME = "refresh_token";
    private static final String ENDPOINT = "/v1/auth";
    private static final int TTL = 3600;

    private AuthCookieAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AuthCookieAdapter(NAME, ENDPOINT, TTL);
    }

    @Test
    void create_shouldBuildSecureHttpOnlyStrictCookieWithTokenAndTtl() {
        var cookie = adapter.create("refresh-1");

        assertThat(cookie.getName()).isEqualTo(NAME);
        assertThat(cookie.getValue()).isEqualTo("refresh-1");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
        assertThat(cookie.getPath()).isEqualTo(ENDPOINT);
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofSeconds(TTL));
    }

    @Test
    void clear_shouldBuildEmptyExpiredCookieWithSameNameAndPath() {
        var cookie = adapter.clear();

        assertThat(cookie.getName()).isEqualTo(NAME);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
        assertThat(cookie.getPath()).isEqualTo(ENDPOINT);
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
    }

    @Test
    void create_shouldSerializeToExpectedSetCookieHeader() {
        assertThat(adapter.create("refresh-1").toString())
            .contains("refresh_token=refresh-1")
            .contains("Path=/v1/auth")
            .contains("Max-Age=3600")
            .contains("Secure")
            .contains("HttpOnly")
            .contains("SameSite=Strict");
    }

    @Test
    void getRefreshTokenTimeToLive_shouldReturnConfiguredTtl() {
        assertThat(adapter.getRefreshTokenTimeToLive()).isEqualTo(TTL);
    }
}