package com.auth.domain.profile.usercase;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;
import com.base.domain.usecase.BaseCrudUsecase;

public class ProfileUsecase extends BaseCrudUsecase<Profile, IProfileRepositoryPort> {

    public ProfileUsecase(IProfileRepositoryPort profileRepositoryPort) {
        super(profileRepositoryPort);
    }
}