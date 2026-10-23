package com.auth.infrastructure.auth.service.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.auth.dto.response.jwt.AuthJwtResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class AuthJwtService {
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String TYPE = "type";
    private static final String ROLES = "roles";

    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration accessTtl;

    public AuthJwtService(
        JwtEncoder encoder,
        @Value("${jwt.issuer}") String issuer,
        @Value("${jwt.access-ttl}") Duration accessTtl) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.accessTtl = accessTtl;
    }

    public AuthJwtResponse getJwtTokens(User user) {
        return new AuthJwtResponse(user,
            generate(user, accessTtl, ACCESS_TOKEN_TYPE));
    }

    @SuppressWarnings("null")
    private String generate(User user, Duration ttl, String type) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(String.valueOf(user.getId()))
            .issuedAt(now)
            .expiresAt(now.plus(ttl))
            .id(UUID.randomUUID().toString())
            .claim(TYPE, type)
            .claim(ROLES, user.getProfiles().stream().map(Profile::getName).toList())
            .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims))
            .getTokenValue();
    }
}
