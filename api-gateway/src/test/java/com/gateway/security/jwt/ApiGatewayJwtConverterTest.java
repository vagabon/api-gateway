package com.gateway.security.jwt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.auth.infrastructure.user.cache.dto.UserCache;
import com.auth.infrastructure.user.cache.service.UserCacheService;
import com.base.domain.exception.MetierException;
import com.base.domain.model.currentuser.CurrentUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiGatewayJwtConverterTest {

    @Mock
    UserCacheService userCache;

    @InjectMocks
    ApiGatewayJwtConverter converter;

    @Test
    @SuppressWarnings("null")
    void convert_shouldBuildAuthenticationWithCurrentUserAndRoleAuthorities() {
        var jwt = jwtWithSubject("42");

        when(userCache.getUser(42L)).thenReturn(
            new UserCache(42L, "gonz", "gonz@example.com",
                List.of("ADMIN", "USER").stream()
                    .collect(Collectors.toSet())));

        var auth = converter.convert(jwt);

        assertThat(auth.getPrincipal())
            .isEqualTo(new CurrentUser(42L, "gonz", "gonz@example.com",
                Set.of("ADMIN", "USER")));
        assertThat(auth.getCredentials()).isSameAs(jwt);
        assertThat(auth.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        verify(userCache).getUser(42L);
    }

    @Test
    void convert_shouldReturnNoAuthorities_whenUserHasNoRole() {
        when(userCache.getUser(1L)).thenReturn(
            new UserCache(1L, "gonz", "gonz@example.com", new HashSet<>()));

        var auth = converter.convert(jwtWithSubject("1"));

        assertThat(auth.getAuthorities()).isEmpty();
    }

    @Test
    void convert_shouldThrowMetierException_whenUserNotInCache() {
        when(userCache.getUser(99L)).thenReturn(null);
        var jwt = jwtWithSubject("99");

        assertThatThrownBy(() -> converter.convert(jwt))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining("ERROR.USER.NOT_FOUND");
    }

    @Test
    void convert_shouldThrowNumberFormatException_whenSubjectIsNotNumeric() {
        var jwt = jwtWithSubject("not-a-number");

        assertThatThrownBy(() -> converter.convert(jwt))
            .isInstanceOf(NumberFormatException.class);
    }

    private static Jwt jwtWithSubject(String subject) {
        return Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject(subject)
            .build();
    }
}