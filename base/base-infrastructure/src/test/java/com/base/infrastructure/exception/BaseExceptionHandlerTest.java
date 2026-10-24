package com.base.infrastructure.exception;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseExceptionHandlerTest {

    private final BaseExceptionHandler handler = new BaseExceptionHandler();

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/test");
    }

    @Test
    void metierException_returnsBadRequestWithMessageAndTargetedStack() {
        var response = handler.handleRuntimeException(
            new MetierException("USER_NOT_FOUND"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("USER_NOT_FOUND");
        assertThat(body.timestamp()).isNotNull();
        assertThat(body.stackTrace()).contains(BaseExceptionHandlerTest.class.getName());
    }

    @Test
    void metierException_withRefreshTokenError_returnsUnauthorized() {
        var response = handler.handleRuntimeException(
            new MetierException(BaseExceptionHandler.ERROR_REFRESH_TOKEN), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message())
            .isEqualTo(BaseExceptionHandler.ERROR_REFRESH_TOKEN);
    }

    @Test
    void accessDenied_returnsUnauthorized() {
        var response = handler.handleRuntimeException(
            new AccessDeniedException("Access Denied"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Access Denied");
    }

    @Test
    void unknownRuntimeException_returnsInternalServerErrorWithFullStack() {
        var response = handler.handleRuntimeException(
            new IllegalStateException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        var body = response.getBody();
        assertThat(body.message()).isEqualTo("boom");
        assertThat(body.stackTrace())
            .contains("java.lang.IllegalStateException: boom")
            .contains("handleRuntimeException".isEmpty() ? "" : "at ");
    }

    @Test
    void constraintViolation_replacesMessageWithConstant() {
        var response = handler.handleRuntimeException(
            new ConstraintViolationException("détail interne", Set.of()), request);

        assertThat(response.getBody().message())
            .isEqualTo(BaseExceptionHandler.ERROR_CONTRAINTS_VIOLATION);
        // comportement actuel : ce n'est pas une MetierException => 500
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void httpClient404_returnsNotFoundWithoutBody() {
        var response = handler.handleRuntimeException(
            new HttpClientErrorException(HttpStatus.NOT_FOUND), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void httpClientOtherStatus_isTreatedAsInternalServerError() {
        var response = handler.handleRuntimeException(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void nullRequest_isSupported() {
        var response = handler.handleRuntimeException(
            new MetierException("ERR"), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void nullRequest_with404_isSupported() {
        var response = handler.handleRuntimeException(
            new HttpClientErrorException(HttpStatus.NOT_FOUND), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}