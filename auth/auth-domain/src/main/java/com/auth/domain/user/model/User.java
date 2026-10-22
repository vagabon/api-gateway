package com.auth.domain.user.model;

import java.time.Instant;
import java.util.List;

import com.auth.domain.profile.model.Profile;

public record User(
        Long id,
        Boolean active,
        String username,
        String email,
        String avatar,
        String googleId,
        Instant lastConnexionDate,
        Instant creationDate,
        Instant deletedDate,
        Instant updatedDate,
        List<Profile> profiles) {
}