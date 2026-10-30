package com.auth.domain.auth.port;

import com.auth.domain.user.user.model.User;

public interface AuthJwtRepositoryPort {
    String generateTokenJwt(User user);

}
