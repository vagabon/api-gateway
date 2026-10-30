package com.auth.infrastructure.profile.adapter;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.ProfileRepositoryPort;
import com.auth.infrastructure.profile.jpa.entity.ProfileJpaEntity;
import com.auth.infrastructure.profile.jpa.repository.ProfileJpaRepository;
import com.auth.infrastructure.profile.mapper.ProfileMapper;
import com.base.domain.exception.MetierException;
import com.base.infrastructure.adapter.BaseCrudAdapter;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileAdapter
    extends BaseCrudAdapter<ProfileJpaEntity, Profile>
    implements ProfileRepositoryPort {

    private static final String USER_PROFILE = "USER";

    @Getter
    private final ProfileJpaRepository repository;
    @Getter
    private final ProfileMapper mapper;

    @Override
    public Profile getProfileUser() {
        return repository.findByName(USER_PROFILE)
            .map(mapper::toDomain).orElseThrow(() -> new MetierException(
                "PROFILE %s not found".formatted(USER_PROFILE)));
    }

    @Override
    public Class<ProfileJpaEntity> getEntityClass() {
        return ProfileJpaEntity.class;
    }
}
