package com.auth.infrastructure.user.controller;

import com.auth.domain.user.model.User;
import com.auth.infrastructure.user.service.UserService;
import com.base.infrastructure.controler.BaseController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User> {

    public UserController(UserService userService) {
        super(userService);
    }
}