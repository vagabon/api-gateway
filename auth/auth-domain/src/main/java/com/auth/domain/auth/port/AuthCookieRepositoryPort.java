package com.auth.domain.auth.port;

public interface AuthCookieRepositoryPort {
    int getRefreshTokenTimeToLive();

    Object createCookie(String refreshToken);

    Object clearCookie();
}
