package com.auth.infrastructure.user.user.controller;

import java.util.List;

import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.user.service.UserService;
import com.base.infrastructure.controller.BaseController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController extends BaseController<User> {

    public UserController(UserService userService) {
        super(userService);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated() && hasRole('USER')")
    public User me(@AuthenticationPrincipal Jwt jwt) {
        var userId = Long.valueOf(jwt.getSubject());
        return service.findById(userId);
    }

    @Override
    @GetMapping
    @PreAuthorize("isAuthenticated() && hasRole('USER')")
    public ResponseEntity<List<User>> findAll() {
        return super.findAll();
    }
}