package com.base.infrastructure.controller;

import com.base.application.DeleteUsecase;
import com.base.domain.model.BaseIdDomain;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DeleteController<D extends BaseIdDomain> {

    DeleteUsecase<D> getDeleteUsecase();

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<Void> delete(@PathVariable Long id) {
        getDeleteUsecase().execute(id);
        return ResponseEntity.noContent().build();
    }
}
