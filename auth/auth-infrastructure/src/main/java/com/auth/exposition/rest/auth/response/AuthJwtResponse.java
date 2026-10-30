package com.auth.exposition.rest.auth.response;

import com.auth.domain.user.user.model.User;

public record AuthJwtResponse(
    User user,
    String jwt) {

}
