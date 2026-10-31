package com.auth.application.user.refresh;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.auth.domain.user.refresh.utils.UserRefreshUtils;
import com.auth.domain.user.user.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserRefreshUsecase {

    private final UserRefreshRepositoryPort userRefreshrepository;

    public String execute(User user, String origin, String ipAdress, int ttl) {
        var userRefresh = new UserRefresh();
        userRefresh.setUser(user);
        userRefresh.setOrigin(origin);
        userRefresh.setToken(UserRefreshUtils.generateRefreshToken());
        userRefresh.setIpAddress(ipAdress);
        userRefresh.setRevoked(false);
        userRefresh.setExpiredDate(Instant.now().plusSeconds(ttl));
        userRefreshrepository.create(userRefresh);
        return userRefresh.getToken();
    }
}
