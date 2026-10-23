package com.base.domain.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TechnicalExceptionTest {
    TechnicalException exception;

    @Test
    void testInstanciate() {
        exception = new TechnicalException();
        exception = new TechnicalException("MESSAGE", new Throwable());
        Assertions.assertEquals("MESSAGE", exception.getMessage());
    }
}
