package com.base.infrastructure.adapter;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import org.hibernate.Session;
import org.hibernate.query.Page;

public interface FindByAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends FindByRepositoryPort<D>, BaseAdapter<E, D> {

    Class<E> getEntityClass();

    EntityManager getEm();

    default PageResult<D> findBy(
        String sql,
        List<Object> values,
        Integer page,
        Integer max) {

        var query = getEm().unwrap(Session.class).createSelectionQuery(sql,
            getEntityClass());
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }

        var total = query.getResultCount();
        var content = query
            .setPage(Page.page(max, page)).getResultList()
            .stream()
            .map(getMapper()::toDomain)
            .toList();
        var totalPages = (int) Math.ceil((double) total / max);

        return new PageResult<>(page, totalPages, total, max, content);
    }

}
