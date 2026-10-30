package com.auth.domain.user.refresh.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import com.base.domain.exception.MetierException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserRefreshUtils {
    private static final String ENCRYPT_ALGORITHM = "SHA-256";

    public static String generateRefreshToken() {
        try {

            var raw = UUID.randomUUID().toString() + System.nanoTime();
            var digest = MessageDigest.getInstance(ENCRYPT_ALGORITHM);
            var hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hash);

        } catch (NoSuchAlgorithmException _) {
            throw new MetierException("REFRESH_HASH_ERROR");
        }
    }
}
