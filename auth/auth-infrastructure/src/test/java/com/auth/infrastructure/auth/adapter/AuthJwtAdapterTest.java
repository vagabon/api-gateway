package com.auth.infrastructure.auth.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthJwtAdapterTest {

    private static final String ISSUER = "https://gateway.test";
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);

    @Mock
    JwtEncoder jwtEncoder;

    private AuthJwtAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AuthJwtAdapter(jwtEncoder, ISSUER, ACCESS_TTL);
    }

    @Test
    void generateTokenJwt_shouldReturnTokenValueFromEncoder() {
        stubEncoder();

        var token = adapter.generateTokenJwt(userWithRoles(42L, "ADMIN"));

        assertThat(token).isEqualTo("jwt-value");
    }

    @Test
    void generateTokenJwt_shouldBuildClaimsFromUser() {
        stubEncoder();
        var before = Instant.now();

        adapter.generateTokenJwt(userWithRoles(42L, "ADMIN", "USER"));

        var claims = captureParameters().getClaims();
        assertThat(claims.getClaimAsString("iss")).isEqualTo(ISSUER);
        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.getClaimAsString("type")).isEqualTo("access");
        assertThat(claims.getClaimAsStringList("roles"))
            .containsExactlyInAnyOrder("ADMIN", "USER");
        assertThat(claims.getId()).isNotBlank();
        assertThat(claims.getIssuedAt()).isBetween(before, Instant.now());
        assertThat(Duration.between(claims.getIssuedAt(), claims.getExpiresAt()))
            .isEqualTo(ACCESS_TTL);
    }

    @Test
    void generateTokenJwt_shouldSignWithHs256() {
        stubEncoder();

        adapter.generateTokenJwt(userWithRoles(1L, "USER"));

        assertThat(captureParameters().getJwsHeader().getAlgorithm())
            .isEqualTo(MacAlgorithm.HS256);
    }

    @Test
    void generateTokenJwt_shouldGenerateDistinctJtiOnEachCall() {
        stubEncoder();
        var user = userWithRoles(1L, "USER");

        adapter.generateTokenJwt(user);
        adapter.generateTokenJwt(user);

        var captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, org.mockito.Mockito.times(2)).encode(captor.capture());
        var ids = captor.getAllValues().stream().map(p -> p.getClaims().getId()).toList();
        assertThat(ids.get(0)).isNotEqualTo(ids.get(1));
    }

    @Test
    void generateTokenJwt_shouldEmitEmptyRoles_whenUserHasNoProfile() {
        stubEncoder();
        var user = new User();
        user.setId(1L);
        user.setProfiles(Set.of());

        adapter.generateTokenJwt(user);

        assertThat(captureParameters().getClaims().getClaimAsStringList("roles"))
            .isEmpty();
    }

    @Test
    void generateTokenJwt_shouldThrowNpe_whenProfilesIsNull() {
        var user = new User();
        user.setId(1L);

        assertThatThrownBy(() -> adapter.generateTokenJwt(user))
            .isInstanceOf(NullPointerException.class);
    }

    private void stubEncoder() {
        var jwt = Jwt.withTokenValue("jwt-value")
            .header("alg", "HS256")
            .claim("sub", "1")
            .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
    }

    private JwtEncoderParameters captureParameters() {
        var captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, atLeastOnce()).encode(captor.capture());
        return captor.getValue();
    }

    private static User userWithRoles(Long id, String... roles) {
        var user = new User();
        user.setId(id);
        var profiles = new java.util.HashSet<Profile>();
        for (var role : roles) {
            var profile = new Profile();
            profile.setName(role);
            profiles.add(profile);
        }
        user.setProfiles(profiles);
        return user;
    }
}