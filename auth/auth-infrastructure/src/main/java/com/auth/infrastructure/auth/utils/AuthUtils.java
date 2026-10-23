package com.auth.infrastructure.auth.utils;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class AuthUtils {

    private static final int PASSWORD_LENGTH = 10;
    private static final int TOKEN_LENGTH = 4;
    private static final String HASHTAG = "#";
    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";

    private static final Random random = new SecureRandom();

    private AuthUtils() {
    }

    public static final String generatToken(int nb) {
        var identityToken = new StringBuilder();
        for (var i = 0; i < nb; i++) {
            identityToken.append(random.nextInt(PASSWORD_LENGTH) + 0);
        }
        return identityToken.toString();
    }

    public static final String getUsername(String login) {
        if (StringUtils.isEmpty(login)) {
            return null;
        }
        return login.trim().replace(SPACE, UNDERSCORE).toLowerCase() + HASHTAG
            + AuthUtils.generatToken(TOKEN_LENGTH);
    }
}
