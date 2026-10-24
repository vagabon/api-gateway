package com.base.domain.port.out;

import java.util.List;

import com.base.domain.model.PageResult;

public interface IBaseRepositoryPortOut<D> {
    D create(D domain);

    D update(D domain);

    D findById(Long id);

    D delete(Long id);

    List<D> findByPage(Integer page, Integer max, String sort);

    PageResult<D> findBy(String sql, List<Object> values, Integer page, Integer max);
}
