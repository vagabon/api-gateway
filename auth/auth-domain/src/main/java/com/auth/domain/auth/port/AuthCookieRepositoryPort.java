package com.auth.domain.auth.port;

public interface AuthCookieRepositoryPort {
    int getRefreshTokenTimeToLive();

    Object create(String refreshToken);

    Object clear();
}
