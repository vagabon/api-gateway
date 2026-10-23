package com.auth.infrastructure.user.service;

import com.auth.domain.user.model.User;
import com.auth.domain.user.usercase.UserUseCase;
import com.auth.infrastructure.user.adapter.UserAdapter;
import com.base.infrastructure.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User> {

    public UserService(UserAdapter userAdapter) {
        super(new UserUseCase(userAdapter));
    }
}
