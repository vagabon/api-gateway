package com.auth.application.user.refresh;

import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUserRefreshByTokenUsecase {

    private final UserRefreshRepositoryPort userRefreshrepository;

    public void execute(String refreshToken) {
        userRefreshrepository.deleteUserRefreshByToken(refreshToken);
    }
}
