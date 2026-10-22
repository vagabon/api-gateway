package com.base.domain.usecase;

public abstract class BaseUseCase {

    protected static final String ADMIN = "ADMIN";

    protected static final String PING = "ping";

    public String ping() {
        return PING;
    }

}
