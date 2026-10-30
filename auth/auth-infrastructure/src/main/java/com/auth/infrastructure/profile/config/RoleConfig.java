package com.auth.infrastructure.profile.config;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.adapter.ProfileAdapter;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleConfig {

    private final ProfileAdapter profileAdapter;

    @Bean
    public FindByUsecase<Profile> roleFindByUsecase() {
        return new FindByUsecase<>(profileAdapter);
    }

    @Bean
    public FindByIdUsecase<Profile> roleFindByIdUsecase() {
        return new FindByIdUsecase<>(profileAdapter);
    }

    @Bean
    public CreateUsecase<Profile> roleCreateUsecase() {
        return new CreateUsecase<>(profileAdapter);
    }

    @Bean
    public UpdateUsecase<Profile> roleUpdateUsecase() {
        return new UpdateUsecase<>(profileAdapter);
    }

}
