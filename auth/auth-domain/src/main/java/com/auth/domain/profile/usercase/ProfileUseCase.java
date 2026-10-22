package com.auth.domain.profile.usercase;

import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;

public class ProfileUseCase {

    protected final IProfileRepositoryPort profileRepositoryPort;

    public ProfileUseCase(IProfileRepositoryPort profileRepositoryPort) {
        this.profileRepositoryPort = profileRepositoryPort;
    }
}