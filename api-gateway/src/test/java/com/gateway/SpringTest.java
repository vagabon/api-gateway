package com.gateway;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpringTest {

    @Test
    void test() {
        assertThat(ApiGatewayApplication.class).isNotNull();
    }
}
