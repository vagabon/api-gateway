package com.gateway.security.jwt;

import java.util.stream.Collectors;

import com.auth.infrastructure.user.user.adapter.UserRedisAdapter;
import com.auth.infrastructure.user.user.redis.RoleCache;
import com.base.domain.exception.MetierException;
import com.base.domain.model.currentuser.CurrentUser;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiGatewayJwtConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String USER_NOT_FOUND = "ERROR.USER.NOT_FOUND";
    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRedisAdapter userCache;

    @Override
    @SuppressWarnings("null")
    public AbstractAuthenticationToken convert(Jwt jwt) {

        var cached = userCache.getUser(Long.valueOf(jwt.getSubject()));
        if (cached == null) {
            throw new MetierException(USER_NOT_FOUND);
        }

        var user = new CurrentUser(cached.id(), cached.username(), cached.email(),
            cached.roles().stream().map(RoleCache::roleName).collect(Collectors.toSet()));

        var authorities = cached.roles().stream()
            .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.roleName()))
            .toList();

        return new UsernamePasswordAuthenticationToken(user, jwt, authorities);
    }
}