package com.auth.infrastructure.user.adapter;

import com.auth.domain.user.model.User;
import com.auth.domain.user.port.out.IUserAdapterPort;
import com.auth.infrastructure.user.mapper.UserMapper;
import com.auth.infrastructure.user.persistence.entity.UserEntity;
import com.auth.infrastructure.user.persistence.repository.UserRepository;
import com.base.infrastructure.adapter.BaseAdapter;

import org.springframework.stereotype.Component;

@Component
public class UserAdapter extends BaseAdapter<UserEntity, User> implements IUserAdapterPort {

    public UserAdapter(UserRepository userRepository, UserMapper userMapper) {
        super(userRepository, userMapper, UserEntity.class);
    }
}