package com.auth.infrastructure.profile.adapter;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.mapper.ProfileMapper;
import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.auth.infrastructure.profile.persistence.repository.ProfileRepository;
import com.base.infrastructure.adapter.BaseAdapter;

import org.springframework.stereotype.Component;

@Component
public class ProfileAdapter extends BaseAdapter<ProfileEntity, Profile> {

    public ProfileAdapter(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        super(profileRepository, profileMapper);
    }
}
