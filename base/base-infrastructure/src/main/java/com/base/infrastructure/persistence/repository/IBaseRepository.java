package com.base.infrastructure.persistence.repository;

import com.base.infrastructure.persistence.entity.BaseEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IBaseRepository<E extends BaseEntity> extends JpaRepository<E, Long> {

}
