package com.base.infrastructure.adapter;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseDomain;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.infrastructure.mapper.BaseMapper;
import com.base.infrastructure.persistence.entity.BaseEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;
import com.base.infrastructure.utils.EntityUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseAdapter<E extends BaseEntity, R extends BaseDomain, J extends IBaseRepository<E>>
    implements IBaseRepositoryPortOut<R> {

    private static final String NOT_FOUND = "ID not found : %d";

    protected final J repository;
    protected final BaseMapper<E, R> mapper;
    private final Class<E> entityClass;

    @PersistenceContext
    private EntityManager em;

    protected BaseAdapter(
        J repository,
        BaseMapper<E, R> mapper,
        Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityClass = entityClass;
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
            .orElseThrow(() -> new MetierException(NOT_FOUND.formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findByPage(Integer page, Integer max, String sort) {
        var pageable = PageRequest.of(page, max, Sort.by(Sort.Direction.ASC, sort));
        return repository.findAll(pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findBy(String sql, List<Object> values, Integer page, Integer max) {
        var query = em.createQuery(sql, entityClass);
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }

        return query.setFirstResult(page).setMaxResults(max).getResultList().stream()
            .map(mapper::toDomain)
            .toList();
    }
}
