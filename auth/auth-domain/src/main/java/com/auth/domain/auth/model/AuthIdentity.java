package com.auth.domain.auth.model;

import com.auth.domain.user.user.model.User;

public record AuthIdentity(
    User user,
    String jwt,
    Object cookie,
    String reftreshToken) {
}