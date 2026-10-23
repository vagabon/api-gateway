package com.auth.domain.user.usercase;

import com.auth.domain.user.model.User;
import com.auth.domain.user.port.out.IUserAdapterPort;
import com.base.domain.usecase.BaseCrudUseCase;

public class UserUseCase extends BaseCrudUseCase<User> {

    public UserUseCase(IUserAdapterPort userRepositoryPort) {
        super(userRepositoryPort);
    }
}