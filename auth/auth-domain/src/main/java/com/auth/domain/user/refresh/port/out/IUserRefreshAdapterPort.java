package com.auth.domain.user.refresh.port.out;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.base.domain.port.out.IBaseRepositoryPortOut;

public interface IUserRefreshAdapterPort extends IBaseRepositoryPortOut<UserRefresh> {

    UserRefresh findByToken(String token);

    long deleteUserRefreshByToken(String token);

    long deleteUserRefreshWhenExpired();
}
