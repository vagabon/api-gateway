package com.base.domain.model.currentuser;

import java.util.Set;

public record CurrentUser(
    Long id,
    String username,
    String email,
    Set<String> roles) {
}
