package com.auth.application.user.user;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;
import com.auth.domain.user.user.port.UserCacheRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedUserUsecase {

    private final UserCacheRepository userCacheRepository;

    public UserLight execute(User user) {
        return userCacheRepository.save(user);
    }
}
