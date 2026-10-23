package com.base.domain.exception;

public class MetierException extends BaseException {

    public MetierException() {
    }

    public MetierException(String message) {
        super(message);
    }

    public MetierException(String message, Throwable cause) {
        super(message, cause);
    }
}
