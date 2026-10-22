package com.auth.infrastructure.profile.service;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.adapter.ProfileAdapter;
import com.base.infrastructure.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class ProfileService extends BaseService<Profile> {

    public ProfileService(ProfileAdapter profileAdapter) {
        super(profileAdapter);
    }
}
