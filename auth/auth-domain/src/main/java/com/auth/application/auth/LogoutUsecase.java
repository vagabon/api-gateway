package com.auth.application.auth;

import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutUsecase {

    private final AuthCookieRepositoryPort authCookieRepository;
    private final DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;

    public Object execute(String refreshToken) {

        deleteUserRefreshByTokenUsecase.execute(refreshToken);

        return authCookieRepository.clear();
    }
}
