package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByPageRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindByPageUsecase<D extends BaseIdDomain> {

    private final FindByPageRepositoryPort<D> findByPageRepositoryPort;

    public PageResult<D> execute(Integer page, Integer max, String sort) {
        return findByPageRepositoryPort.findByPage(page, max, sort);
    }

}
