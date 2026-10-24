package com.base.infrastructure.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.Valid;

import com.base.domain.model.BaseDomain;
import com.base.domain.model.PageResult;
import com.base.domain.user.CurrentUser;
import com.base.infrastructure.service.BaseService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class BaseController<D extends BaseDomain> {

    protected final BaseService<D, ?> service;

    protected BaseController(BaseService<D, ?> service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        var ping = service.ping();
        return ResponseEntity.ok(ping);
    }

    @GetMapping
    public ResponseEntity<List<D>> findAll() {
        var entities = service.findAll(0, 500, "id");
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<D> findById(@PathVariable Long id) {
        D entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/findBy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResult<D>> findBy(
        @RequestParam(defaultValue = "") String fields,
        @RequestParam(defaultValue = "") String values,
        @RequestParam(required = false) Integer first,
        @RequestParam(required = false) Integer max) {
        var entities = service.findBy(fields, Arrays.asList((Object[]) values.split(",")),
            first, max);
        return ResponseEntity.ok(entities);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<D> create(@Valid @RequestBody D request) {
        var userConnected = getCurrentUser();
        D entities = service.create(userConnected, request);
        return ResponseEntity.ok(entities);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<D> update(@Valid @RequestBody D request) {
        var userConnected = getCurrentUser();
        D entities = service.update(userConnected, request);
        return ResponseEntity.ok(entities);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        var userConnected = getCurrentUser();
        var isDeleted = service.delete(userConnected, id);
        return ResponseEntity.ok(isDeleted);
    }

    @DeleteMapping("/desactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<D> desactivate(@PathVariable Long id) {
        var userConnected = getCurrentUser();
        D desactivate = service.desactivate(userConnected, id);
        return ResponseEntity.ok(desactivate);
    }

    public CurrentUser getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.getPrincipal() instanceof CurrentUser user
                ? user
                : null;
    }
}
