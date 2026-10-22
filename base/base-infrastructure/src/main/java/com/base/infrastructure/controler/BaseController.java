package com.base.infrastructure.controler;

import java.util.List;

import jakarta.validation.Valid;

import com.base.domain.model.BaseDomain;
import com.base.infrastructure.service.BaseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<R extends BaseDomain> {

    private final BaseService<R> service;

    protected BaseController(BaseService<R> service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        var ping = service.ping();
        return ResponseEntity.ok(ping);
    }

    @GetMapping
    public ResponseEntity<List<R>> findAll() {
        var profiles = service.findAll(0, 500, "id");
        return ResponseEntity.ok(profiles);
    }

    @PostMapping
    public ResponseEntity<R> create(@Valid @RequestBody R request) {
        var profiles = service.create(request);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping
    public ResponseEntity<R> update(@Valid @RequestBody R request) {
        var profiles = service.update(request);
        return ResponseEntity.ok(profiles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        var isDeleted = service.delete(id);
        return ResponseEntity.ok(isDeleted);
    }

    @DeleteMapping("/desactivate/{id}")
    public ResponseEntity<R> desactivate(@PathVariable Long id) {
        var desactivate = service.desactivate(id);
        return ResponseEntity.ok(desactivate);
    }
}
