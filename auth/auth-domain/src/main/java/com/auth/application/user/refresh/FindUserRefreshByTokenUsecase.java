package com.auth.application.user.refresh;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.base.domain.exception.MetierException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindUserRefreshByTokenUsecase {
    public static final String USER_NOT_FOUND = "ERROR.USER.NOT_FOUND";

    private final UserRefreshRepositoryPort userRefreshrepository;

    public UserRefresh execute(String refreshToken) {

        var user = userRefreshrepository.findByToken(refreshToken);
        if (user == null) {
            throw new MetierException(USER_NOT_FOUND);
        }

        return user;
    }
}
