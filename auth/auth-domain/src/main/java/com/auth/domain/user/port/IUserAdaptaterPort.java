package com.auth.domain.user.port;

import java.util.Optional;

import com.auth.domain.user.model.User;

public interface IUserAdaptaterPort {
    Optional<User> findByEmail(String email);

    User save(User user);
}