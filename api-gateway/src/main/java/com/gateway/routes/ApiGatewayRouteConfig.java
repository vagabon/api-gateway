package com.gateway.routes;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.auth.infrastructure.user.cache.service.UserCacheService;
import com.gateway.routes.header.ApiGatewayRouteHeader;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiGatewayRouteConfig extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_NAME = "X-User-Name";
    private static final String X_USER_EMAIL = "X-User-Email";
    private static final String X_USER_ROLES = "X-User-Roles";
    private static final String ERROR_JWT = "JWT invalide : {}";
    private static final String SEPARATE = ",";

    private final JwtDecoder jwtDecoder;
    private final UserCacheService userCache;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain)
        throws ServletException, IOException {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(BEARER)) {
            try {
                var subject = jwtDecoder.decode(authorization.substring(7)).getSubject();

                var user = userCache.getUser(Long.valueOf(subject));

                if (user != null) {
                    headers.put(X_USER_ID, String.valueOf(user.id()));
                    headers.put(X_USER_NAME, user.username());
                    headers.put(X_USER_EMAIL, user.email());
                    headers.put(X_USER_ROLES, String.join(SEPARATE, user.roles()));
                }

            } catch (JwtException e) {
                log.error(ERROR_JWT, e.getMessage());
            }
        }
        chain.doFilter(new ApiGatewayRouteHeader(request, headers), response);
    }

}
