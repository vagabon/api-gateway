package com.base.infrastructure.adapter;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.infrastructure.mapper.BaseMapper;
import com.base.infrastructure.persistence.entity.BaseEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;
import com.base.infrastructure.utils.EntityUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseAdapter<E extends BaseEntity, R extends BaseDomain>
        implements IBaseRepositoryPortOut<R> {

    private static final String NOT_FOUND = "Not found : id = %d";

    private final IBaseRepository<E> repository;
    private final BaseMapper<E, R> mapper;

    protected BaseAdapter(IBaseRepository<E> repository, BaseMapper<E, R> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public R create(R domain) {
        var entity = mapper.toEntity(domain);
        repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional
    public R update(R domain) {
        var entity = mapper.toEntity(domain);
        var entityFromBdd = entityFindById(domain.getId());
        EntityUtils.setEntity(entityFromBdd, entity, false);
        var merged = repository.save(entityFromBdd);
        return mapper.toDomain(merged);
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        var entity = entityFindById(id);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional
    public R delete(Long id) {
        var entity = entityFindById(id);
        repository.delete(entity);
        return mapper.toDomain(entity);
    }

    private E entityFindById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND.formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findByPage(Integer page, Integer max, String sort) {
        var pageable = PageRequest.of(page, max, Sort.by(Sort.Direction.ASC, sort));
        return repository.findAll(pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<R> constructQuery(Integer first, Integer max, String field, Object[] values) {
        throw new UnsupportedOperationException("Unimplemented method 'constructQuery'");
    }
}
