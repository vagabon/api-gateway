package com.auth.domain.auth.model;

public record AuthGoogle(
    String sub,
    String aud,
    String email,
    Boolean emailVerified,
    String givenName,
    String picture) {
}
