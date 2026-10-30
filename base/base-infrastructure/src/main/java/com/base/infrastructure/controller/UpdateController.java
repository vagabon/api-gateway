package com.base.infrastructure.controller;

import jakarta.validation.Valid;

import com.base.application.UpdateUsecase;
import com.base.domain.model.BaseCrudDomain;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UpdateController<D extends BaseCrudDomain> {

    UpdateUsecase<D> getUpdateUsecase();

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<D> update(@Valid @RequestBody D request) {
        D entities = getUpdateUsecase().execute(request);
        return ResponseEntity.ok(entities);
    }
}
