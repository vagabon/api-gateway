package com.base.infrastructure.adapter;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseDomain;
import com.base.domain.model.PageResult;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.infrastructure.mapper.BaseMapper;
import com.base.infrastructure.persistence.entity.BaseEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;
import com.base.infrastructure.utils.EntityUtils;

import org.hibernate.Session;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseAdapter<E extends BaseEntity, D extends BaseDomain, R extends IBaseRepository<E>>
    implements IBaseRepositoryPortOut<D> {

    private static final String NOT_FOUND = "ID not found : %d";

    protected final R repository;
    protected final BaseMapper<E, D> mapper;
    private final Class<E> entityClass;

    @PersistenceContext
    private EntityManager em;

    protected BaseAdapter(
        R repository,
        BaseMapper<E, D> mapper,
        Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public D create(D domain) {
        var entity = mapper.toEntity(domain);
        repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional
    public D update(D domain) {
        var entity = mapper.toEntity(domain);
        var entityFromBdd = entityFindById(domain.getId());
        EntityUtils.setEntity(entityFromBdd, entity, false);
        var merged = repository.save(entityFromBdd);
        return mapper.toDomain(merged);
    }

    @Override
    @Transactional
    public D delete(Long id) {
        var entity = entityFindById(id);
        repository.delete(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public D findById(Long id) {
        var entity = entityFindById(id);
        return mapper.toDomain(entity);
    }

    private E entityFindById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new MetierException(NOT_FOUND.formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<D> findByPage(Integer page, Integer max, String sort) {
        var pageable = PageRequest.of(page, max, Sort.by(Sort.Direction.ASC, sort));
        return repository.findAll(pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<D> findBy(
        String sql,
        List<Object> values,
        Integer page,
        Integer max) {

        var query = em.unwrap(Session.class).createSelectionQuery(sql, entityClass);
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }

        var total = query.getResultCount();
        var content = query
            .setPage(Page.page(max, page)).getResultList()
            .stream()
            .map(mapper::toDomain)
            .toList();
        var totalPages = (int) Math.ceil((double) total / max);

        return new PageResult<>(page, totalPages, total, max, content);
    }
}
