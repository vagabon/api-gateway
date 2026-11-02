package com.auth.application.user.refresh;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.base.domain.exception.MetierException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindUserRefreshByTokenUsecase {
    public static final String USER_REFRESH_NOT_FOUND = "ERROR.USER_REFRESH.NOT_FOUND";
    public static final String USER_REFRESH_REVOKED = "ERROR.USER_REFRESH.REVOKED";
    public static final String USER_REFRESH_EXPIRED = "ERROR.USER_REFRESH.EXPIRED";

    private final UserRefreshRepositoryPort userRefreshrepository;

    public UserRefresh execute(String refreshToken) {

        var userRefresh = userRefreshrepository.findByToken(refreshToken);
        if (userRefresh == null) {
            throw new MetierException(USER_REFRESH_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(userRefresh.getRevoked())) {
            throw new MetierException(USER_REFRESH_REVOKED);
        }
        if (userRefresh.getExpiredDate().isBefore(Instant.now())) {
            throw new MetierException(USER_REFRESH_EXPIRED);
        }

        return userRefresh;
    }
}
