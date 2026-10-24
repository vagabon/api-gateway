package com.auth.infrastructure.auth.service.jwt;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthJwtServiceTest {

    private static final String ISSUER = "my-issuer";
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);

    @Mock
    private JwtEncoder encoder;

    private AuthJwtService service;

    @BeforeEach
    void setUp() {
        service = new AuthJwtService(encoder, ISSUER, ACCESS_TTL);
    }

    private Jwt fakeJwt(String value) {
        var now = Instant.now();
        return new Jwt(value, now, now.plusSeconds(60),
            Map.of("alg", "HS256"), Map.of("sub", "1"));
    }

    private User user(Long id, String... profileNames) {
        var user = new User();
        user.setId(id);
        user.setProfiles(Arrays.stream(profileNames).map(name -> {
            var profile = new Profile();
            profile.setName(name);
            return profile;
        }).toList());
        return user;
    }

    private JwtEncoderParameters captureParameters() {
        var captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(encoder).encode(captor.capture());
        return captor.getValue();
    }

    @Test
    void getJwtTokens_returnsResponseWithUser() {
        var user = user(1L, "USER");
        var jwt = fakeJwt("signed-token");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        var response = service.getJwtTokens(user);

        assertThat(response).isNotNull();
    }

    @Test
    void getJwtTokens_buildsExpectedClaims() {
        var user = user(42L, "ADMIN", "USER");
        var jwt = fakeJwt("t");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        var before = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        service.getJwtTokens(user);

        var after = Instant.now();
        var claims = captureParameters().getClaims();

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.getId()).isNotBlank();
        assertThat(claims.<String>getClaim("type")).isEqualTo("access");
        assertThat(claims.<List<String>>getClaim("roles")).containsExactly("ADMIN",
            "USER");
        assertThat(claims.getIssuedAt()).isBetween(before, after);
        assertThat(claims.getExpiresAt())
            .isBetween(before.plus(ACCESS_TTL), after.plus(ACCESS_TTL));
    }

    @Test
    void getJwtTokens_usesHs256Header() {
        var jwt = fakeJwt("t");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        service.getJwtTokens(user(1L, "USER"));

        assertThat(captureParameters().getJwsHeader().getAlgorithm())
            .isEqualTo(MacAlgorithm.HS256);
    }

    @Test
    void getJwtTokens_generatesUniqueJtiEachTime() {
        var jwt = fakeJwt("t");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        var user = user(1L, "USER");

        service.getJwtTokens(user);
        service.getJwtTokens(user);

        var captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(encoder, times(2)).encode(captor.capture());
        var ids = captor.getAllValues().stream().map(p -> p.getClaims().getId()).toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    void getJwtTokens_withoutProfiles_hasEmptyRoles() {
        var jwt = fakeJwt("t");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        service.getJwtTokens(user(1L));

        assertThat(captureParameters().getClaims().<List<String>>getClaim("roles"))
            .isEmpty();
    }
}