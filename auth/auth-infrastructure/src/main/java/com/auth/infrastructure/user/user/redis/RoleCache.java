package com.auth.infrastructure.user.user.redis;

import java.io.Serializable;

public record RoleCache(
    String name,
    String roleName,
    String origin)
    implements Serializable {
}
