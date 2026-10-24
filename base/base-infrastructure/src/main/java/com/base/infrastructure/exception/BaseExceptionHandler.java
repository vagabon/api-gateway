package com.base.infrastructure.exception;

import java.time.Instant;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

public class BaseExceptionHandler {
    public static final String ERROR_REFRESH_TOKEN = "NO_REFRESH_TOKEN";
    public static final String ERROR_CONTRAINTS_VIOLATION = "ERRORS.CONTRAINTS_VIOLATION";
    private static final String METIER_EXCEPTION = "MetierException";
    private static final String PACKAGE = "com.";
    private static final String UNKNOWN = "unknown";

    private static final Logger log = LoggerFactory
        .getLogger(BaseExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @SuppressWarnings("null")
    public ResponseEntity<ExceptionResponse> handleRuntimeException(
        RuntimeException exception,
        HttpServletRequest request) {
        var message = exception.getMessage();
        var endpoint = request != null ? request.getRequestURI() : UNKNOWN;

        if (exception instanceof HttpClientErrorException clientEx
            && clientEx.getStatusCode().value() == 404) {
            log.error("Resource not found for endpoint : {} - message : {}", endpoint,
                clientEx.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (exception instanceof ConstraintViolationException) {
            message = ERROR_CONTRAINTS_VIOLATION;
        }

        var httpStatus = HttpStatus.BAD_REQUEST;

        String targetedStack;
        if (exception.getClass().getName().contains(METIER_EXCEPTION)) {
            targetedStack = Arrays.stream(exception.getStackTrace())
                .filter(trace -> trace.getClassName().contains(PACKAGE))
                .findFirst()
                .map(StackTraceElement::toString)
                .orElse(getStackTraceAsString(exception));
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            targetedStack = getStackTraceAsString(exception);
        }

        if (ERROR_REFRESH_TOKEN.equals(message)) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if ("Access Denied".equals(message)) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            log.error("EXCEPTION : {}", targetedStack);
        }

        var responseBody = new ExceptionResponse(Instant.now(), message, targetedStack);
        return ResponseEntity.status(httpStatus).body(responseBody);
    }

    private String getStackTraceAsString(Throwable throwable) {
        var sw = new java.io.StringWriter();
        var pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    record ExceptionResponse(
        Instant timestamp,
        String message,
        String stackTrace) {
    }
}