package com.base.domain.port;

import com.base.domain.model.pageresult.PageResult;

public interface FindByPageRepositoryPort<D> {
    PageResult<D> findByPage(Integer page, Integer max, String sort);
}
