package com.auth.infrastructure.auth.config;

import com.auth.application.auth.AuthenticateRefreshUsecase;
import com.auth.application.auth.AuthenticateUsecase;
import com.auth.application.auth.GoogleConnectUsecase;
import com.auth.application.auth.LogoutUsecase;
import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.application.user.refresh.FindUserRefreshByTokenUsecase;
import com.auth.application.user.user.CreateUserWithGoogleUsecase;
import com.auth.infrastructure.auth.adapter.AuthCookieAdapter;
import com.auth.infrastructure.auth.adapter.AuthJwtAdapter;
import com.auth.infrastructure.auth.api.client.AuthGoogleClient;
import com.auth.infrastructure.user.user.adapter.UserAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthConfig {
    private final UserAdapter userAdapter;
    private final AuthJwtAdapter authJwtAdapter;
    private final AuthCookieAdapter authCookieAdapter;
    private final AuthGoogleClient authGoogleclient;

    private final CreateUserWithGoogleUsecase createUserWithGoogleUsecase;
    private final CreateUserRefreshUsecase createUserRefreshUsecase;
    private final DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;

    private final FindUserRefreshByTokenUsecase findUserRefreshByTokenUsecase;

    @Bean
    public GoogleConnectUsecase googleConnectUsecase() {
        return new GoogleConnectUsecase(authGoogleclient);
    }

    @Bean
    public AuthenticateUsecase authenticateUsecase() {
        return new AuthenticateUsecase(userAdapter, authJwtAdapter, authCookieAdapter,
            createUserWithGoogleUsecase,
            createUserRefreshUsecase);
    }

    @Bean
    public AuthenticateRefreshUsecase authenticateRefreshUsecase() {
        return new AuthenticateRefreshUsecase(userAdapter, authJwtAdapter,
            authCookieAdapter,
            findUserRefreshByTokenUsecase,
            deleteUserRefreshByTokenUsecase,
            createUserRefreshUsecase);
    }

    @Bean
    public LogoutUsecase logoutUsecase() {
        return new LogoutUsecase(authCookieAdapter, deleteUserRefreshByTokenUsecase);
    }
}
