package com.base.infrastructure.controller;

import jakarta.validation.Valid;

import com.base.application.CreateUsecase;
import com.base.domain.model.BaseCrudDomain;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CreateController<D extends BaseCrudDomain> {

    CreateUsecase<D> getCreateUsecase();

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<D> create(@Valid @RequestBody D request) {
        D entities = getCreateUsecase().execute(request);
        return ResponseEntity.ok(entities);
    }
}
