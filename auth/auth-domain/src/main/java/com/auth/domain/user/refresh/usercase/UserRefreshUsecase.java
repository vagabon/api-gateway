package com.auth.domain.user.refresh.usercase;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.in.IUserRefreshUsecasePort;
import com.auth.domain.user.refresh.port.out.IUserRefreshAdapterPort;
import com.base.domain.usecase.BaseCrudUsecase;

public class UserRefreshUsecase
    extends BaseCrudUsecase<UserRefresh, IUserRefreshAdapterPort>
    implements IUserRefreshUsecasePort {

    public UserRefreshUsecase(IUserRefreshAdapterPort userRefreshAdapter) {
        super(userRefreshAdapter);
    }

    @Override
    public UserRefresh findByToken(String token) {
        return adapter.findByToken(token);
    }

    @Override
    public long deleteUserRefreshByToken(String token) {
        return adapter.deleteUserRefreshByToken(token);
    }

    @Override
    public long deleteUserRefreshWhenExpired() {
        return adapter.deleteUserRefreshWhenExpired();
    }
}
