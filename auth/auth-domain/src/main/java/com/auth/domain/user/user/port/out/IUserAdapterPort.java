package com.auth.domain.user.user.port.out;

import com.auth.domain.user.user.model.User;
import com.base.domain.port.out.IBaseRepositoryPortOut;

public interface IUserAdapterPort extends IBaseRepositoryPortOut<User> {
    User findByGoogleId(String googleId);
}