package com.auth.infrastructure.user.refresh.config;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.refresh.DeleteUserRefreshByTokenUsecase;
import com.auth.application.user.refresh.FindUserRefreshByTokenUsecase;
import com.auth.infrastructure.user.refresh.adapter.UserRefreshAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRefreshConfig {

    private final UserRefreshAdapter userRefreshrepository;

    @Bean
    public CreateUserRefreshUsecase createUserRefreshUsecase() {
        return new CreateUserRefreshUsecase(userRefreshrepository);
    }

    @Bean
    public DeleteUserRefreshByTokenUsecase deleteUserRefreshByTokenUsecase() {
        return new DeleteUserRefreshByTokenUsecase(userRefreshrepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public FindUserRefreshByTokenUsecase findUserRefreshByTokenUsecase() {
        return new FindUserRefreshByTokenUsecase(userRefreshrepository);
    }

}
