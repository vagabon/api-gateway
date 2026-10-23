package com.auth.infrastructure.auth.dto.response.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthGoogleResponse(
    String sub,
    String email,
    @JsonProperty("given_name") String givenName,
    String picture) {
}
