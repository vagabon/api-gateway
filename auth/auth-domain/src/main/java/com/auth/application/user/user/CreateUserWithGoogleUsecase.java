package com.auth.application.user.user;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.utils.AuthUtils;
import com.auth.domain.role.port.RoleRepositoryPort;
import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserWithGoogleUsecase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public User execute(AuthGoogle authGoogle, String origin) {
        var user = new User();
        user.setGoogleId(authGoogle.sub());
        user.setUsername(AuthUtils.getUsername(authGoogle.givenName()));
        user.setEmail(authGoogle.email());
        user.setAvatar(authGoogle.picture());
        user.setLastConnexionDate(Instant.now());
        var userRole = roleRepository.getRoleForUser(origin);
        if (userRole != null) {
            user.setRoles(new HashSet<>(List.of(userRole)));
        }
        return userRepository.create(user);
    }
}
