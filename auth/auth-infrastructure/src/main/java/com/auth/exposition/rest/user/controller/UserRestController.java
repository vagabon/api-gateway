package com.auth.exposition.rest.user.controller;

import com.auth.domain.user.user.model.User;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;
import com.base.infrastructure.controller.CreateController;
import com.base.infrastructure.controller.FindByController;
import com.base.infrastructure.controller.FindByIdController;
import com.base.infrastructure.controller.UpdateController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserRestController
    implements FindByController<User>, FindByIdController<User>, CreateController<User>,
    UpdateController<User> {

    @Getter
    private final FindByUsecase<User> findByUsecase;
    @Getter
    private final FindByIdUsecase<User> findByIdUsecase;
    @Getter
    private final CreateUsecase<User> createUsecase;
    @Getter
    private final UpdateUsecase<User> updateUsecase;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated() && hasRole('USER')")
    public User me(@AuthenticationPrincipal Jwt jwt) {
        var userId = Long.valueOf(jwt.getSubject());
        return getFindByIdUsecase().execute(userId);
    }
}
