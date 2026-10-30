package com.base.application;

import java.util.List;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindByUsecase<D extends BaseIdDomain> {

    private final FindByRepositoryPort<D> findByRepositoryPort;

    public PageResult<D> execute(
        String sql,
        List<Object> values,
        Integer page,
        Integer max) {
        return findByRepositoryPort.findBy(sql, values, page, max);
    }

}
