package com.auth.infrastructure.profile.adapter;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;
import com.auth.infrastructure.profile.mapper.ProfileMapper;
import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.auth.infrastructure.profile.persistence.repository.ProfileRepository;
import com.base.domain.exception.MetierException;
import com.base.infrastructure.adapter.BaseAdapter;

import org.springframework.stereotype.Component;

@Component
public class ProfileAdapter extends BaseAdapter<ProfileEntity, Profile, ProfileRepository>
    implements IProfileRepositoryPort {

    private static final String USER_PROFILE = "USER";

    public ProfileAdapter(
        ProfileRepository profileRepository,
        ProfileMapper profileMapper) {
        super(profileRepository, profileMapper, ProfileEntity.class);
    }

    public Profile getProfileUser() {
        return repository.findByName(USER_PROFILE)
            .map(mapper::toDomain).orElseThrow(() -> new MetierException(
                "PROFILE %s not found".formatted(USER_PROFILE)));
    }
}
