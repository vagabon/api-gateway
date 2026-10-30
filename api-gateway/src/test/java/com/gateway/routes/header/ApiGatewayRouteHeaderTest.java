package com.gateway.routes.header;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ApiGatewayRouteHeaderTest {

    @Test
    void getHeader_shouldKeepOriginalHeaders() {
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer abc");

        var wrapper = new ApiGatewayRouteHeader(req, Map.of());

        assertThat(wrapper.getHeader("Authorization")).isEqualTo("Bearer abc");
    }

    @Test
    void getHeader_shouldBeCaseInsensitive() {
        var req = new MockHttpServletRequest();
        req.addHeader("Content-Type", "application/json");

        var wrapper = new ApiGatewayRouteHeader(req, Map.of("X-User-Id", "42"));

        assertThat(wrapper.getHeader("content-type")).isEqualTo("application/json");
        assertThat(wrapper.getHeader("x-user-id")).isEqualTo("42");
    }

    @Test
    void constructor_shouldDiscardSpoofedXUserHeadersFromClient() {
        var req = new MockHttpServletRequest();
        req.addHeader("X-User-Id", "999");
        req.addHeader("x-user-role", "ADMIN");

        var wrapper = new ApiGatewayRouteHeader(req, Map.of("x-user-id", "42"));

        assertThat(wrapper.getHeader("X-User-Id")).isEqualTo("42");
        assertThat(Collections.list(wrapper.getHeaders("x-user-id")))
            .containsExactly("42");
        assertThat(wrapper.getHeader("x-user-role")).isNull();
    }

    @Test
    void getHeaders_shouldReturnAllValuesOfMultiValuedHeader() {
        var req = new MockHttpServletRequest();
        req.addHeader("Accept", "text/html");
        req.addHeader("Accept", "application/json");

        var wrapper = new ApiGatewayRouteHeader(req, Map.of());

        assertThat(Collections.list(wrapper.getHeaders("accept")))
            .containsExactly("text/html", "application/json");
    }

    @Test
    void getHeader_shouldReturnNull_whenHeaderAbsent() {
        var wrapper = new ApiGatewayRouteHeader(new MockHttpServletRequest(), Map.of());

        assertThat(wrapper.getHeader("X-Unknown")).isNull();
    }

    @Test
    void getHeaders_shouldReturnEmptyEnumeration_whenHeaderAbsent() {
        var wrapper = new ApiGatewayRouteHeader(new MockHttpServletRequest(), Map.of());

        assertThat(Collections.list(wrapper.getHeaders("X-Unknown"))).isEmpty();
    }

    @Test
    void getHeaderNames_shouldContainOriginalAndUserHeaders_withoutSpoofedOnes() {
        var req = new MockHttpServletRequest();
        req.addHeader("Host", "gateway");
        req.addHeader("X-User-Spoofed", "1");

        var wrapper = new ApiGatewayRouteHeader(req,
            Map.of("x-user-id", "42", "x-user-email", "gonz@example.com"));

        assertThat(Collections.list(wrapper.getHeaderNames()))
            .containsExactlyInAnyOrder("Host", "x-user-id", "x-user-email");
    }
}