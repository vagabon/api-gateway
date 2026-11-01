package com.auth.domain.auth.utils;

import java.security.SecureRandom;
import java.util.Random;

public interface AuthUtils {

    final int PASSWORD_LENGTH = 10;
    final int TOKEN_LENGTH = 4;
    final String HASHTAG = "#";
    final String SPACE = " ";
    final String UNDERSCORE = "_";

    final Random random = new SecureRandom();

    default String generatToken(int nb) {
        var identityToken = new StringBuilder();
        for (var i = 0; i < nb; i++) {
            identityToken.append(random.nextInt(PASSWORD_LENGTH) + 0);
        }
        return identityToken.toString();
    }

    default String getUsername(String login) {
        if (login == null || login.isBlank()) {
            return null;
        }
        return login.trim().replace(SPACE, UNDERSCORE).toLowerCase() + HASHTAG
            + generatToken(TOKEN_LENGTH);
    }
}
