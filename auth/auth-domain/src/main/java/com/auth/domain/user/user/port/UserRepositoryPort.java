package com.auth.domain.user.user.port;

import com.auth.domain.user.user.model.User;
import com.base.domain.port.CreateRepositoryPort;
import com.base.domain.port.UpdateRepositoryPort;

public interface UserRepositoryPort
    extends CreateRepositoryPort<User>, UpdateRepositoryPort<User> {
    User findByGoogleId(String googleId);
}