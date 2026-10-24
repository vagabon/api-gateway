package com.auth.infrastructure.user.refresh.controller;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.infrastructure.user.refresh.service.UserRefreshService;
import com.base.infrastructure.controller.BaseController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-refresh")
public class UserRefreshController extends BaseController<UserRefresh> {

    public UserRefreshController(UserRefreshService userRefreshService) {
        super(userRefreshService);
    }
}