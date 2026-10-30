package com.auth.domain.user.refresh.port;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.base.domain.port.CreateRepositoryPort;

public interface UserRefreshRepositoryPort extends CreateRepositoryPort<UserRefresh> {

    UserRefresh findByToken(String token);

    long deleteUserRefreshByToken(String token);

    long deleteUserRefreshWhenExpired();
}
