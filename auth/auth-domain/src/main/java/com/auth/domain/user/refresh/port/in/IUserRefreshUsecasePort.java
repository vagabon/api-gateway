package com.auth.domain.user.refresh.port.in;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.base.domain.port.in.IBaseUsecase;

public interface IUserRefreshUsecasePort extends IBaseUsecase<UserRefresh> {

    UserRefresh findByToken(String token);

    long deleteUserRefreshByToken(String token);

    long deleteUserRefreshWhenExpired();

}
