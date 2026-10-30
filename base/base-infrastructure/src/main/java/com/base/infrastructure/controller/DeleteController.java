package com.base.infrastructure.controller;

import com.base.application.DeleteUsecase;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DeleteController {

    DeleteUsecase getDeleteUsecase();

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<Boolean> delete(@PathVariable Long id) {
        var isDeleted = getDeleteUsecase().execute(id);
        return ResponseEntity.ok(isDeleted);
    }
}
