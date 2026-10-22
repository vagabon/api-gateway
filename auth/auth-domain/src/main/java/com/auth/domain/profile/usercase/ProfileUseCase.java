package com.auth.domain.profile.usercase;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;
import com.base.domain.usecase.BaseCrudUseCase;

public class ProfileUseCase extends BaseCrudUseCase<Profile> {

    public ProfileUseCase(IProfileRepositoryPort profileRepositoryPort) {
        super(profileRepositoryPort);
    }
}