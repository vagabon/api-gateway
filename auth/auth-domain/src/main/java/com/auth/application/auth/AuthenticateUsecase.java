package com.auth.application.auth;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.user.CreateUserWithGoogleUsecase;
import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.model.AuthIdentity;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.role.port.RoleRepositoryPort;
import com.auth.domain.user.user.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateUsecase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final AuthJwtRepositoryPort authJwtRepository;
    private final AuthCookieRepositoryPort authCookieRepository;

    private final CreateUserWithGoogleUsecase createUserWithGoogleUsecase;
    private final CreateUserRefreshUsecase createUserRefreshUsecase;

    public AuthIdentity execute(AuthGoogle authGoogle, String origin, String ipAdress) {

        var user = userRepository.findByGoogleId(authGoogle.sub());
        if (user == null) {
            user = createUserWithGoogleUsecase.execute(authGoogle, origin);
        } else {
            user.updateUserAvatar(authGoogle.picture());
            var roleForOrigin = user.getRolesFromOrigin(origin);
            if (roleForOrigin.isEmpty()) {
                var userRole = roleRepository.getRoleForUser(origin);
                if (userRole != null) {
                    user.getRoles().add(userRole);
                    roleForOrigin.add(userRole);
                }
            }
            userRepository.update(user);
            user.setRoles(roleForOrigin);
        }

        var refreshToken = createUserRefreshUsecase.execute(user, origin, ipAdress,
            authCookieRepository.getRefreshTokenTimeToLive());

        var jwt = authJwtRepository.generateTokenJwt(user, origin);

        var cookie = authCookieRepository.create(refreshToken);

        return new AuthIdentity(user, jwt, cookie, refreshToken);
    }
}
