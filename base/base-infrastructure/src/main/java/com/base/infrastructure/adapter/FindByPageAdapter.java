package com.base.infrastructure.adapter;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByPageRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public interface FindByPageAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends FindByPageRepositoryPort<D>, BaseAdapter<E, D> {

    default PageResult<D> findByPage(Integer page, Integer max, String sort) {
        var pageable = PageRequest.of(page, max, Sort.by(Sort.Direction.ASC, sort));

        var content = getRepository().findAll(pageable).stream()
            .map(getMapper()::toDomain)
            .toList();

        return new PageResult<>(page, 0, 0L, max, content);
    }
}
