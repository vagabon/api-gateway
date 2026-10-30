package com.base.infrastructure.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.base.domain.model.BaseIdDomain;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import lombok.Getter;

public abstract class BaseCrudAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    implements CreateAdapter<E, D>, UpdateAdapter<E, D>, DesactivateAdapter<E, D>,
    FindByIdAdapter<E, D>, FindByAdapter<E, D> {

    @PersistenceContext
    @Getter
    private EntityManager em;

}
