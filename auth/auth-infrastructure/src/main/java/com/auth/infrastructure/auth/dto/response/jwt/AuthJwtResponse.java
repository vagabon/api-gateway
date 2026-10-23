package com.auth.infrastructure.auth.dto.response.jwt;

import com.auth.domain.user.user.model.User;

public record AuthJwtResponse(
    User user,
    String jwt) {

}
