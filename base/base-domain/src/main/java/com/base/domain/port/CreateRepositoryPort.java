package com.base.domain.port;

import com.base.domain.model.BaseIdDomain;

public interface CreateRepositoryPort<D extends BaseIdDomain> {
    D create(D domain);
}
