package com.auth.infrastructure.user.refresh.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.refresh-cookie")
public record UserRefreshProperties(
    String name,
    String endpoint,
    int ttl) {
}
