package com.auth.domain.user.user.usercase;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.in.IUserUsecasePort;
import com.auth.domain.user.user.port.out.IUserAdapterPort;
import com.base.domain.usecase.BaseCrudUsecase;

public class UserUsecase extends BaseCrudUsecase<User, IUserAdapterPort>
    implements IUserUsecasePort {

    public UserUsecase(IUserAdapterPort userAdapterPort) {
        super(userAdapterPort);
    }
}