package com.base.infrastructure.mapper;

public interface BaseMapper<E, D> {
    D toDomain(E entity);

    E toEntity(D domain);

}
