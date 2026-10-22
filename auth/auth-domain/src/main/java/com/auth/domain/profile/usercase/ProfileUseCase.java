package com.auth.domain.profile.usercase;

import java.util.List;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.profile.port.outbound.IProfileRepositoryPort;

public class ProfileUseCase {

    protected final IProfileRepositoryPort profileRepositoryPort;

    public ProfileUseCase(IProfileRepositoryPort profileRepositoryPort) {
        this.profileRepositoryPort = profileRepositoryPort;
    }

    public List<Profile> findAllProfiles() {
        return profileRepositoryPort.findByPage(1, 1, "id asc");
    }
}