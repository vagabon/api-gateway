package com.auth.application.user.user;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.utils.AuthUtils;
import com.auth.domain.profile.port.ProfileRepositoryPort;
import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserWithGoogleUsecase {

    private final UserRepositoryPort userRepository;
    private final ProfileRepositoryPort profileRepository;

    public User execute(AuthGoogle authGoogle) {
        var user = new User();
        user.setGoogleId(authGoogle.sub());
        user.setUsername(AuthUtils.getUsername(authGoogle.givenName()));
        user.setEmail(authGoogle.email());
        user.setAvatar(authGoogle.picture());
        user.setLastConnexionDate(Instant.now());
        var userProfile = profileRepository.getProfileUser();
        if (userProfile != null) {
            user.setProfiles(new HashSet<>(Arrays.asList(userProfile)));
        }
        return userRepository.create(user);
    }
}
