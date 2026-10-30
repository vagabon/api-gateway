package com.base.infrastructure.controller;

import java.util.List;
import java.util.Set;

import com.base.domain.model.currentuser.CurrentUser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserControllerTest {

    private final CurrentUserController controller = new CurrentUserController() {
    };

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_shouldReturnPrincipal_whenPrincipalIsCurrentUser() {
        var user = new CurrentUser(42L, "gonz", "gonz@example.com", Set.of("USER"));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        assertThat(controller.getCurrentUser()).isSameAs(user);
    }

    @Test
    void getCurrentUser_shouldReturnNull_whenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThat(controller.getCurrentUser()).isNull();
    }

    @Test
    void getCurrentUser_shouldReturnNull_whenPrincipalIsAnotherType() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("someone", null, List.of()));

        assertThat(controller.getCurrentUser()).isNull();
    }

    @Test
    void getCurrentUser_shouldReturnNull_whenAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
            new AnonymousAuthenticationToken("key", "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThat(controller.getCurrentUser()).isNull();
    }
}