package com.base.domain.port;

import java.util.List;

import com.base.domain.model.pageresult.PageResult;

public interface FindByRepositoryPort<D> {
    PageResult<D> findBy(String fields, List<Object> values, Integer page, Integer max);
}
