package com.auth.infrastructure.user.user.service;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.usercase.UserUsecase;
import com.auth.infrastructure.user.user.adapter.UserAdapter;
import com.base.infrastructure.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, UserUsecase> {

    public UserService(UserAdapter userAdapter) {
        super(new UserUsecase(userAdapter));
    }
}
