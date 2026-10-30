package com.base.domain.port;

import java.util.List;

import com.base.domain.model.pageresult.PageResult;

public interface FindByRepositoryPort<D> {
    PageResult<D> findBy(String sql, List<Object> values, Integer page, Integer max);
}
