package com.base.application;

import com.base.domain.port.DeleteRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUsecase {

    private final DeleteRepositoryPort deleteRepositoryPort;

    public boolean execute(Long id) {
        return deleteRepositoryPort.delete(id);
    }
}
