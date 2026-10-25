package com.gateway.security;

import java.util.Arrays;
import java.util.List;

import com.gateway.security.jwt.ApiGatewayJwtConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApiSecurityConfig {

    private static final String CORS_REGISTER = "/**";
    private static final String ENDPOINT_PERMIT = "/**";

    private final String origins;
    private final String allowedHeaders;
    private final String allowedMethods;

    public ApiSecurityConfig(
        @Value("${app.cors.origins}") String origins,
        @Value("${app.cors.allowed-headers}") String allowedHeaders,
        @Value("${app.cors.allowed-methods}") String allowedMethods) {
        this.origins = origins;
        this.allowedHeaders = allowedHeaders;
        this.allowedMethods = allowedMethods;
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        ApiGatewayJwtConfig converter) {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // NOSONAR: API stateless with bearer
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(ENDPOINT_PERMIT).permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(
                oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(converter)))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origins));
        config.setAllowedHeaders(List.of(allowedHeaders));
        config.setAllowedMethods(
            Arrays.asList(allowedMethods.split(StringUtils.SPACE)));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CORS_REGISTER, config);
        return source;
    }
}
