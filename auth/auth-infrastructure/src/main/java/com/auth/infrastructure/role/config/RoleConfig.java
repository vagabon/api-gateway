package com.auth.infrastructure.role.config;

import com.auth.domain.role.model.Role;
import com.auth.infrastructure.role.adapter.RoleAdapter;
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

    private final RoleAdapter roleAdapter;

    @Bean
    public FindByUsecase<Role> roleFindByUsecase() {
        return new FindByUsecase<>(roleAdapter);
    }

    @Bean
    public FindByIdUsecase<Role> roleFindByIdUsecase() {
        return new FindByIdUsecase<>(roleAdapter);
    }

    @Bean
    public CreateUsecase<Role> roleCreateUsecase() {
        return new CreateUsecase<>(roleAdapter);
    }

    @Bean
    public UpdateUsecase<Role> roleUpdateUsecase() {
        return new UpdateUsecase<>(roleAdapter);
    }

}
