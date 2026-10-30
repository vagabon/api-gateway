package com.base.infrastructure.jpa.repository;

import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseJpaRepository<E extends BaseCrudJpaEntity>
    extends JpaRepository<E, Long> {
}
