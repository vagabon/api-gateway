package com.auth.infrastructure.profile.controller;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.service.ProfileService;
import com.base.infrastructure.controller.BaseController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/roles")
public class ProfileController extends BaseController<Profile> {

    public ProfileController(ProfileService profileService) {
        super(profileService);
    }
}