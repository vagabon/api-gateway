package com.base.infrastructure.controller;

import java.util.List;

import com.base.application.FindByUsecase;
import com.base.domain.model.BaseCrudDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.infrastructure.utils.query.utils.QueryUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface FindByController<D extends BaseCrudDomain> extends QueryUtils {
    static final Integer DEFAULT_PAGE = 0;
    static final Integer DEFAULT_MAX = 100;
    static final String SEPARATOR = ",";

    FindByUsecase<D> getFindByUsecase();

    @GetMapping("/findBy")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<PageResult<D>> findBy(
        @RequestParam(defaultValue = "") String fields,
        @RequestParam(defaultValue = "") String values,
        @RequestParam(required = false) Integer first,
        @RequestParam(required = false) Integer max) {

        if (first == null) {
            first = DEFAULT_PAGE;
        }
        if (max == null || max > DEFAULT_MAX) {
            max = DEFAULT_MAX;
        }

        var entities = getFindByUsecase().execute(fields,
            List.of((Object[]) values.split(SEPARATOR)),
            first, max);

        return ResponseEntity.ok(entities);
    }
}
