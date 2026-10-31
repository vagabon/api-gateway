package com.auth.exposition.rest.role.controller;

import com.auth.domain.role.model.Role;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;
import com.base.infrastructure.controller.CreateController;
import com.base.infrastructure.controller.FindByController;
import com.base.infrastructure.controller.FindByIdController;
import com.base.infrastructure.controller.UpdateController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleRestController implements FindByController<Role>,
    FindByIdController<Role>, CreateController<Role>, UpdateController<Role> {

    @Getter
    private final FindByUsecase<Role> findByUsecase;
    @Getter
    private final FindByIdUsecase<Role> findByIdUsecase;
    @Getter
    private final CreateUsecase<Role> createUsecase;
    @Getter
    private final UpdateUsecase<Role> updateUsecase;

}