package com.auth.infrastructure.user.cache.dto;

import java.io.Serializable;
import java.util.List;

public record CacheUser(
    Long id,
    String username,
    String email,
    List<String> roles)
    implements Serializable {
}