package com.base.domain.user;

import java.util.Set;

public record CurrentUser(
    Long id,
    String username,
    String email,
    Set<String> roles) {
}
