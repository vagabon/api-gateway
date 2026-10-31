package com.auth.infrastructure.user.user.redis;

import java.io.Serializable;
import java.util.Set;

public record UserCache(
    Long id,
    String username,
    String email,
    Set<RoleCache> roles)
    implements Serializable {
}