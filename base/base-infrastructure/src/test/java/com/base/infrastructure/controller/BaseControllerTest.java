package com.base.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BaseControllerTest {

    private final BaseController controller = new BaseController() {
    };

    @Test
    void ping_shouldReturn200WithEmptyBody() {
        var response = controller.ping();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}