package com.auth.infrastructure.auth.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class AuthJwtAdapter implements AuthJwtRepositoryPort {

    private static final String TYPE = "type";
    private static final String ROLES = "roles";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration accessTtl;

    public AuthJwtAdapter(
        JwtEncoder jwtEncoder,
        @Value("${app.jwt.issuer}") String issuer,
        @Value("${app.jwt.access-ttl}") Duration accessTtl) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTtl = accessTtl;
    }

    @Override
    public String generateTokenJwt(User user) {

        var now = Instant.now();

        @SuppressWarnings("null")
        var claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(String.valueOf(user.getId()))
            .issuedAt(now)
            .expiresAt(now.plus(accessTtl))
            .id(UUID.randomUUID().toString())
            .claim(TYPE, ACCESS_TOKEN_TYPE)
            .claim(ROLES, user.getProfiles().stream().map(Profile::getName).toList())
            .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
            .getTokenValue();
    }

}
