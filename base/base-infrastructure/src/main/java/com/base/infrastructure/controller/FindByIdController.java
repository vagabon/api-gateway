package com.base.infrastructure.controller;

import com.base.application.FindByIdUsecase;
import com.base.domain.model.BaseIdDomain;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface FindByIdController<D extends BaseIdDomain> {

    FindByIdUsecase<D> getFindByIdUsecase();

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<D> findById(@PathVariable Long id) {
        D entity = getFindByIdUsecase().execute(id);
        return ResponseEntity.ok(entity);
    }
}
