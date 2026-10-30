package com.base.infrastructure.controller;

import com.base.domain.model.currentuser.CurrentUser;

import org.springframework.security.core.context.SecurityContextHolder;

public interface CurrentUserController {

    default CurrentUser getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.getPrincipal() instanceof CurrentUser user
                ? user
                : null;
    }
}
