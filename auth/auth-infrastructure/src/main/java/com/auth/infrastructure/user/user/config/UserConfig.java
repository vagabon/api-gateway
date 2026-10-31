package com.auth.infrastructure.user.user.config;

import com.auth.application.user.user.CachedUserUsecase;
import com.auth.application.user.user.CreateUserWithGoogleUsecase;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.role.adapter.RoleAdapter;
import com.auth.infrastructure.user.user.adapter.UserAdapter;
import com.auth.infrastructure.user.user.adapter.UserMongoAdapter;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserConfig {

    private final UserAdapter userRepository;
    private final UserMongoAdapter userMongoRepository;
    private final RoleAdapter roleRepository;

    @Bean
    public FindByUsecase<User> userFindByUsecase() {
        return new FindByUsecase<>(userRepository);
    }

    @Bean
    public FindByIdUsecase<User> userFindByIdUsecase() {
        return new FindByIdUsecase<>(userRepository);
    }

    @Bean
    public CreateUsecase<User> userCreateUsecase() {
        return new CreateUsecase<>(userRepository);
    }

    @Bean
    public UpdateUsecase<User> userUpdateUsecase() {
        return new UpdateUsecase<>(userRepository);
    }

    @Bean
    public CreateUserWithGoogleUsecase createUserWithGoogleUsecase() {
        return new CreateUserWithGoogleUsecase(userRepository, roleRepository);
    }

    @Bean
    public CachedUserUsecase cachedUserUsecase() {
        return new CachedUserUsecase(userMongoRepository);
    }
}
