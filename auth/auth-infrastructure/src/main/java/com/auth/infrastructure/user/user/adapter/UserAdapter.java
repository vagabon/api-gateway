package com.auth.infrastructure.user.user.adapter;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.out.IUserAdapterPort;
import com.auth.infrastructure.user.user.mapper.UserMapper;
import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.auth.infrastructure.user.user.persistence.repository.UserRepository;
import com.base.infrastructure.adapter.BaseAdapter;

import org.springframework.stereotype.Component;

@Component
public class UserAdapter extends BaseAdapter<UserEntity, User, UserRepository>
    implements IUserAdapterPort {

    private final UserRepository userRepository;

    public UserAdapter(UserRepository userRepository, UserMapper userMapper) {
        super(userRepository, userMapper, UserEntity.class);
        this.userRepository = userRepository;
    }

    @Override
    public User findByGoogleId(String googleId) {
        var userEntity = userRepository.findByGoogleId(googleId)
            .orElse(null);
        return mapper.toDomain(userEntity);
    }

    public User findByUsername(String username) {
        var userEntity = userRepository.findByUsername(username)
            .orElse(null);
        return mapper.toDomain(userEntity);
    }
}