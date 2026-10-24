package com.auth.domain.user.refresh.port.in;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.user.model.User;
import com.base.domain.port.in.IBaseUsecase;

public interface IUserRefreshUsecasePort extends IBaseUsecase<UserRefresh> {

    String generateUserRefresh(User user, int ttl);

    UserRefresh findByToken(String token);

    long deleteByToken(String token);

    long deleteExpiredTokens();

}
