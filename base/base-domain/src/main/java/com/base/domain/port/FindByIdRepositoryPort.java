package com.base.domain.port;

public interface FindByIdRepositoryPort<D> {
    D findById(Long id);
}
