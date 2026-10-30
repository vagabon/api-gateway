package com.base.domain.port;

public interface CreateRepositoryPort<D> {
    D create(D domain);
}
