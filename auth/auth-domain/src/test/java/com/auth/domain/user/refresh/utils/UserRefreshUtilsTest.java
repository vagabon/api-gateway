package com.auth.domain.user.refresh.utils;

import java.util.Base64;
import java.util.HashSet;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRefreshUtilsTest {

    @Test
    void generateRefreshToken_shouldReturnUrlSafeBase64Of43Chars() {
        var token = UserRefreshUtils.generateRefreshToken();

        assertThat(token).matches("[A-Za-z0-9_-]{43}");
    }

    @Test
    void generateRefreshToken_shouldDecodeTo32Bytes() {
        var token = UserRefreshUtils.generateRefreshToken();

        assertThat(Base64.getUrlDecoder().decode(token)).hasSize(32);
    }

    @Test
    void generateRefreshToken_shouldBeUniqueAcrossManyCalls() {
        var tokens = new HashSet<String>();

        IntStream.range(0, 10_000)
            .forEach(i -> tokens.add(UserRefreshUtils.generateRefreshToken()));

        assertThat(tokens).hasSize(10_000);
    }
}
