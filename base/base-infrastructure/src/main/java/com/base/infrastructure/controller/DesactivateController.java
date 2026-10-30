package com.base.infrastructure.controller;

import com.base.application.DesactivateUsecase;
import com.base.domain.model.BaseCrudDomain;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DesactivateController<D extends BaseCrudDomain> {

    DesactivateUsecase<D> getDesactivateUsecase();

    @DeleteMapping("/desactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<D> desactivate(@PathVariable Long id) {
        D desactivate = getDesactivateUsecase().execute(id);
        return ResponseEntity.ok(desactivate);
    }
}
