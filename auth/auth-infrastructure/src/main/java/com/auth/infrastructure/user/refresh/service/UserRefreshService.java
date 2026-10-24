package com.auth.infrastructure.user.refresh.service;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.in.IUserRefreshUsecasePort;
import com.auth.domain.user.refresh.usercase.UserRefreshUsecase;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.refresh.adapter.UserRefreshAdapter;
import com.base.infrastructure.service.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRefreshService extends BaseService<UserRefresh, UserRefreshUsecase>
    implements IUserRefreshUsecasePort {

    public UserRefreshService(
        UserRefreshAdapter userRefreshAdapter) {
        super(new UserRefreshUsecase(userRefreshAdapter));
    }

    @Override
    @Transactional
    public String generateUserRefresh(User user, int ttl) {
        return usecase.generateUserRefresh(user, ttl);
    }

    @Override
    @Transactional
    public long deleteByToken(String userToken) {
        return usecase.deleteByToken(userToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRefresh findByToken(String refreshToken) {
        return usecase.findByToken(refreshToken);
    }

    @Override
    @Transactional
    public long deleteExpiredTokens() {
        return usecase.deleteExpiredTokens();
    }
}
