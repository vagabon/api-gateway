package com.auth.exposition.rest.role.controller;

import com.auth.domain.profile.model.Profile;
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
public class RoleRestController implements FindByController<Profile>,
    FindByIdController<Profile>, CreateController<Profile>, UpdateController<Profile> {

    @Getter
    private final FindByUsecase<Profile> findByUsecase;
    @Getter
    private final FindByIdUsecase<Profile> findByIdUsecase;
    @Getter
    private final CreateUsecase<Profile> createUsecase;
    @Getter
    private final UpdateUsecase<Profile> updateUsecase;

}