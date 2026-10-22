package com.auth.domain.user.usercase;

import java.time.Instant;
import java.util.Arrays;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.model.User;
import com.auth.domain.user.port.IUserAdaptaterPort;
import com.auth.domain.user.port.IUserUseCasePort;

public class UserUseCase implements IUserUseCasePort {

    private final IUserAdaptaterPort userRepositoryPort;

    public UserUseCase(IUserAdaptaterPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User getOrCreateUserFromOAuth(String email, String name, String avatarUrl, String googleId) {
        return userRepositoryPort.findByEmail(email)
                .orElseGet(() -> createUser(email, name, avatarUrl, googleId));
    }

    private User createUser(String email, String name, String avatarUrl, String googleId) {
        var defaultProfile = new Profile();

        User newUser = new User(null, true, name, email, avatarUrl, googleId, Instant.now(), Instant.now(),
                null, null, Arrays.asList(defaultProfile));
        return userRepositoryPort.save(newUser);

    }
}