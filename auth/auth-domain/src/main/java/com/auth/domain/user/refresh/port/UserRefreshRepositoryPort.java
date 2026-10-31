package com.auth.domain.user.refresh.port;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.base.domain.port.CreateRepositoryPort;

public interface UserRefreshRepositoryPort extends CreateRepositoryPort<UserRefresh> {

    UserRefresh findByTokenAndOrigin(String token, String origin);

    long deleteUserRefreshByToken(String token);

    long deleteUserRefreshWhenExpired();
}
