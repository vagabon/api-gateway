package com.auth.infrastructure.auth.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.auth.dto.response.google.AuthGoogleResponse;
import com.auth.infrastructure.auth.utils.AuthUtils;
import com.auth.infrastructure.profile.adapter.ProfileAdapter;
import com.auth.infrastructure.user.user.adapter.UserAdapter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAdapter userAdapter;
    private final ProfileAdapter profileAdapter;

    public AuthService(
        UserAdapter userRepository,
        ProfileAdapter profileAdapter) {
        this.userAdapter = userRepository;
        this.profileAdapter = profileAdapter;
    }

    @Transactional
    public User googleConnect(AuthGoogleResponse google) {
        var user = userAdapter.findByGoogleId(google.sub());
        if (user != null) {
            user.setAvatar(google.picture());
            user.setLastConnexionDate(Instant.now());
            return user;
        }
        return createUser(google);
    }

    private User createUser(AuthGoogleResponse google) {
        var user = new User();
        user.setGoogleId(google.sub());
        user.setUsername(AuthUtils.getUsername(google.givenName()));
        user.setEmail(google.email());
        user.setAvatar(google.picture());
        user.setLastConnexionDate(Instant.now());
        var userProfile = profileAdapter.getProfileUser();
        if (userProfile != null) {
            user.setProfiles(new ArrayList<>(Arrays.asList(userProfile)));
        }
        return userAdapter.create(user);
    }
}