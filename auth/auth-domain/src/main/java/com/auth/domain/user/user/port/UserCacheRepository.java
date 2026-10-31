package com.auth.domain.user.user.port;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;

public interface UserCacheRepository {
    UserLight save(User user);
}
