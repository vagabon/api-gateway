package com.base.infrastructure.adapter;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;
import com.base.infrastructure.utils.query.dto.QueryDto;
import com.base.infrastructure.utils.query.utils.QueryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.Session;
import org.hibernate.query.Page;

public interface FindByAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends FindByRepositoryPort<D>, BaseAdapter<E, D>, QueryUtils {

    Class<E> getEntityClass();

    EntityManager getEm();

    default PageResult<D> findBy(
        String fields,
        List<Object> values,
        Integer page,
        Integer max) {

        var endQuery = new StringBuilder();
        var queryDtos = initQueryDto(endQuery, fields, values);
        var hql = getSqlQuery(queryDtos, false, endQuery);
        var parameters = getParameters(queryDtos, values);

        var query = getEm().unwrap(Session.class).createSelectionQuery(hql,
            getEntityClass());
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
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

    private List<Object> getParameters(
        List<QueryDto> queryDtos,
        List<Object> values) {
        var parameters = new ArrayList<Object>();
        for (var i = 0; i < values.size(); i++) {
            var value = values.get(i);
            if (BooleanUtils.isTrue(queryDtos.get(i).getLike())) {
                value = getLike(value.toString());
            } else if ("true".equals(value)) {
                value = Boolean.TRUE;
            } else if ("false".equals(value)) {
                value = Boolean.FALSE;
            }
            parameters.add(value);
        }
        return parameters;
    }

}
