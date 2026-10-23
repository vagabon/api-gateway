package com.base.domain.port.out;

import java.util.List;

public interface IBaseRepositoryPortOut<R> {
    R create(R domain);

    R update(R domain);

    R findById(Long id);

    R delete(Long id);

    List<R> findByPage(Integer page, Integer max, String sort);

    List<R> findBy(String sql, List<Object> values, Integer page, Integer max);
}
