package com.base.infrastructure.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUseCase;
import com.base.domain.usecase.BaseCrudUseCase;
import com.base.infrastructure.utils.query.IQueryUtils;
import com.base.infrastructure.utils.query.QueryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<R extends BaseDomain> implements IBaseUseCase<R>, IQueryUtils {

    protected final BaseCrudUseCase<R> useCase;

    protected BaseService(BaseCrudUseCase<R> useCase) {
        this.useCase = useCase;
    }

    @Override
    public String ping() {
        return useCase.ping();
    }

    @Override
    @Transactional
    public R create(R domain) {
        return useCase.create(domain);
    }

    @Override
    @Transactional
    public R update(R domain) {
        if (domain.getId() == null) {
            throw new EntityNotFoundException("id is null");
        }
        return useCase.update(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        return useCase.findById(id);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return useCase.delete(id);
    }

    @Override
    @Transactional
    public R desactivate(Long id) {
        return useCase.desactivate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAll(Integer page, Integer max, String sort) {
        return useCase.findAll(page, max, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findBy(String fields, List<Object> fieldsValues, Integer first, Integer max) {
        // var userConnected = getUserConnected();
        if (first == null) {
            first = 0;
        }
        if (max == null) {
            max = 100;
        }

        var endQuery = new StringBuilder();
        var queryDtos = initQueryDto(endQuery, fields, fieldsValues.toArray());
        var sqlQuery = getSqlQuery(queryDtos, false, endQuery);
        var values = new ArrayList<Object>();
        for (var i = 0; i < fieldsValues.size(); i++) {
            var value = fieldsValues.get(i);
            if (BooleanUtils.isTrue(queryDtos.get(i).getLike())) {
                value = QueryUtils.getLike(value.toString());
            } else if ("true".equals(value)) {
                value = Boolean.TRUE;
            } else if ("false".equals(value)) {
                value = Boolean.FALSE;
            }
            values.add(value);
        }

        return useCase.findBy(sqlQuery, values, first, max);
    }
}
