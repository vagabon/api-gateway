package com.auth.application.auth;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.application.user.refresh.FindUserRefreshByTokenUsecase;
import com.auth.domain.auth.model.AuthIdentity;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.user.user.port.UserRepositoryPort;
import com.base.domain.exception.MetierException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateRefreshUsecase {
    public static final String ERROR_REFRESH_TOKEN = "NO_REFRESH_TOKEN";

    private final UserRepositoryPort userRepository;
    private final AuthJwtRepositoryPort authJwtRepository;
    private final AuthCookieRepositoryPort authCookieRepository;

    private final FindUserRefreshByTokenUsecase findUserRefreshByTokenUsecase;
    private final DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;
    private final CreateUserRefreshUsecase createUserRefreshUsecase;

    public AuthIdentity execute(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new MetierException(ERROR_REFRESH_TOKEN);
        }

        var userRefresk = findUserRefreshByTokenUsecase.execute(refreshToken);

        deleteUserRefreshByTokenUsecase.execute(refreshToken);

        var user = userRefresk.getUser();
        user.updateLastConnexion();
        userRepository.update(user);

        var newRefreshToken = createUserRefreshUsecase.execute(userRefresk.getUser(),
            authCookieRepository.getRefreshTokenTimeToLive());

        var jwt = authJwtRepository.generateTokenJwt(
            userRefresk.getUser());

        var cookie = authCookieRepository.create(newRefreshToken);

        return new AuthIdentity(userRefresk.getUser(), jwt, cookie, newRefreshToken);
    }
}
