package com.auth.infrastructure.user.cache.dto;

import java.io.Serializable;
import java.util.Set;

public record UserCache(
    Long id,
    String username,
    String email,
    Set<String> roles)
    implements Serializable {
}