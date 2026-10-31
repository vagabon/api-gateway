package com.base.infrastructure.exception;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import com.base.domain.exception.MetierException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

public class BaseExceptionHandler {
    public static final List<String> ERROR_401 = List.of("ERROR.USER_REFRESH.NOT_FOUND",
        "ERROR.USER_REFRESH.REVOKED", "ERROR.USER_REFRESH.EXPIRED",
        "ERROR.USER_REFRESH.EMPTY", "Access Denied");
    public static final String ERROR_CONTRAINTS_VIOLATION = "ERRORS.CONTRAINTS_VIOLATION";
    public static final String ERROR_RESSOURCE_ACCESS = "ERRORS.RESSOURCE_ACCESS : %s";
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

        if ((exception instanceof HttpClientErrorException clientEx
            && clientEx.getStatusCode().value() == 404)) {
            log.error("Resource not found for endpoint : {} - message : {}", endpoint,
                clientEx.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var httpStatus = HttpStatus.BAD_REQUEST;

        if (exception instanceof ConstraintViolationException) {
            message = ERROR_CONTRAINTS_VIOLATION;
        }

        if (exception instanceof ResourceAccessException) {
            message = ERROR_RESSOURCE_ACCESS.formatted(endpoint);
            httpStatus = HttpStatus.NOT_FOUND;
        }

        String targetedStack;
        if (exception instanceof MetierException metierException) {
            targetedStack = Arrays.stream(metierException.getStackTrace())
                .filter(trace -> trace.getClassName().contains(PACKAGE))
                .findFirst()
                .map(StackTraceElement::toString)
                .orElse(getStackTraceAsString(metierException));
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            targetedStack = getStackTraceAsString(exception);
        }

        if (ERROR_401.indexOf(message) != -1) {
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