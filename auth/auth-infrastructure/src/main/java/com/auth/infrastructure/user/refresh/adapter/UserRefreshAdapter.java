package com.auth.infrastructure.user.refresh.adapter;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.out.IUserRefreshAdapterPort;
import com.auth.infrastructure.user.refresh.mapper.UserRefreshMapper;
import com.auth.infrastructure.user.refresh.persistence.entity.UserRefreshEntity;
import com.auth.infrastructure.user.refresh.persistence.repository.UserRefreshRepository;
import com.base.infrastructure.adapter.BaseAdapter;

import org.springframework.stereotype.Component;

@Component
public class UserRefreshAdapter
    extends BaseAdapter<UserRefreshEntity, UserRefresh, UserRefreshRepository>
    implements IUserRefreshAdapterPort {

    public UserRefreshAdapter(
        UserRefreshRepository userRefreshRepository,
        UserRefreshMapper userRefreshMapper) {
        super(userRefreshRepository, userRefreshMapper, UserRefreshEntity.class);
    }

    public UserRefresh findByToken(String token) {
        var userRefresh = repository
            .findByTokenAndRevokedFalseAndExpiredDateAfter(token, Instant.now())
            .orElse(null);
        return mapper.toDomain(userRefresh);
    }

    public long deleteUserRefreshByToken(String token) {
        return repository
            .deleteByToken(token);
    }

    public long deleteUserRefreshWhenExpired() {
        return repository
            .deleteByExpiredDateBefore(Instant.now());
    }

}
