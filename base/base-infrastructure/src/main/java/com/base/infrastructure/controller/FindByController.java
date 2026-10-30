package com.base.infrastructure.controller;

import java.util.ArrayList;

import com.base.application.FindByUsecase;
import com.base.domain.model.BaseCrudDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.infrastructure.utils.query.IQueryUtils;
import com.base.infrastructure.utils.query.QueryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface FindByController<D extends BaseCrudDomain> extends IQueryUtils {

    FindByUsecase<D> getFindByUsecase();

    @GetMapping("/findBy")
    @PreAuthorize("hasRole('ADMIN')")
    default ResponseEntity<PageResult<D>> findBy(
        @RequestParam(defaultValue = "") String fields,
        @RequestParam(defaultValue = "") String fieldsValue,
        @RequestParam(required = false) Integer first,
        @RequestParam(required = false) Integer max) {

        if (first == null) {
            first = 0;
        }
        if (max == null) {
            max = 100;
        }

        var fieldsValues = (Object[]) fieldsValue.split(",");

        var endQuery = new StringBuilder();
        var queryDtos = initQueryDto(endQuery, fields, fieldsValues);
        var sqlQuery = getSqlQuery(queryDtos, false, endQuery);
        var values = new ArrayList<Object>();
        for (var i = 0; i < fieldsValues.length; i++) {
            var value = fieldsValues[i];
            if (BooleanUtils.isTrue(queryDtos.get(i).getLike())) {
                value = QueryUtils.getLike(value.toString());
            } else if ("true".equals(value)) {
                value = Boolean.TRUE;
            } else if ("false".equals(value)) {
                value = Boolean.FALSE;
            }
            values.add(value);
        }

        var entities = getFindByUsecase().execute(sqlQuery, values, first, max);

        return ResponseEntity.ok(entities);
    }
}
