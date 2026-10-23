package com.auth.infrastructure.auth.dto.response.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthGoogleHttpResponse(
    String iss,
    String aud,
    String sub,
    String email,
    @JsonProperty("email_verified") boolean emailVerified,
    String name,
    String picture,
    @JsonProperty("given_name") String givenName,
    @JsonProperty("family_name") String familyName) {
}