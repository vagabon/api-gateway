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
    public static final String ERROR_REFRESH_TOKEN = "ERROR.USER_REFRESH.EMPTY";

    private final UserRepositoryPort userRepository;
    private final AuthJwtRepositoryPort authJwtRepository;
    private final AuthCookieRepositoryPort authCookieRepository;

    private final FindUserRefreshByTokenUsecase findUserRefreshByTokenUsecase;
    private final DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase;
    private final CreateUserRefreshUsecase createUserRefreshUsecase;

    public AuthIdentity execute(String refreshToken, String origin, String ipAdress) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new MetierException(ERROR_REFRESH_TOKEN);
        }

        var userRefresh = findUserRefreshByTokenUsecase.execute(refreshToken, origin);

        deleteUserRefreshByTokenUsecase.execute(refreshToken);

        var user = userRefresh.getUser();
        user.updateLastConnexion();
        userRepository.update(user);

        var newRefreshToken = createUserRefreshUsecase.execute(user,
            origin, ipAdress, authCookieRepository.getRefreshTokenTimeToLive());

        var jwt = authJwtRepository.generateTokenJwt(
            user, origin);

        var cookie = authCookieRepository.create(newRefreshToken);

        user.setRoles(user.getRolesFromOrigin(origin));

        return new AuthIdentity(user, jwt, cookie, newRefreshToken);
    }
}
