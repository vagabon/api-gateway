package com.auth.domain.user.port;

import com.auth.domain.user.model.User;

public interface IUserUseCasePort {
    User getOrCreateUserFromOAuth(String email, String name, String avatarUrl, String googleId);
}