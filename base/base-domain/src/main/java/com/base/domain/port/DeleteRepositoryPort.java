package com.base.domain.port;

import com.base.domain.model.BaseIdDomain;

public interface DeleteRepositoryPort<D extends BaseIdDomain> {
    void delete(D domain);
}
