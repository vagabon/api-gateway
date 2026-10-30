package com.auth.application.auth;

import java.time.Instant;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.user.CreateUserWithGoogleUsecase;
import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.model.AuthIdentity;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.user.user.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateUsecase {

    private final UserRepositoryPort userRepository;
    private final AuthJwtRepositoryPort authJwtRepository;
    private final AuthCookieRepositoryPort authCookieRepository;

    private final CreateUserWithGoogleUsecase createUserWithGoogleUsecase;
    private final CreateUserRefreshUsecase createUserRefreshUsecase;

    public AuthIdentity execute(AuthGoogle authGoogle) {

        var user = userRepository.findByGoogleId(authGoogle.sub());
        if (user == null) {
            user = createUserWithGoogleUsecase.execute(authGoogle);
        } else {
            user.setAvatar(authGoogle.picture());
            user.setLastConnexionDate(Instant.now());
        }

        var refreshToken = createUserRefreshUsecase.execute(user,
            authCookieRepository.getRefreshTokenTimeToLive());

        var jwt = authJwtRepository.generateTokenJwt(user);

        var cookie = authCookieRepository.create(refreshToken);

        return new AuthIdentity(user, jwt, cookie, refreshToken);
    }
}
