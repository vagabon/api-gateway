package com.auth.infrastructure.user.refresh.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.usercase.UserRefreshUsecase;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.refresh.adapter.UserRefreshAdapter;
import com.base.domain.exception.MetierException;
import com.base.infrastructure.service.BaseService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

@Service
public class UserRefreshService extends BaseService<UserRefresh, UserRefreshUsecase> {
    public static final String REFRESH_TOKEN_ERROR = "NO_REFRESH_TOKEN";
    private static final String ENCRYPT_ALGORITHM = "SHA-256";

    @Getter
    private final int refreshTokenTtl;

    public UserRefreshService(
        UserRefreshAdapter userRefreshAdapter,
        @Value("${jwt.refresh-ttl}") int refreshTokenTtl) {
        super(new UserRefreshUsecase(userRefreshAdapter));
        this.refreshTokenTtl = refreshTokenTtl;
    }

    @Transactional
    public String generateUserRefresh(User user) {
        var userRefresh = new UserRefresh();
        userRefresh.setUser(user);
        var raw = UUID.randomUUID().toString() + System.nanoTime();
        try {
            var digest = MessageDigest.getInstance(ENCRYPT_ALGORITHM);
            var hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            userRefresh.setToken(Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hash));
            userRefresh.setIpAddress(StringUtils.EMPTY);
            userRefresh.setRevoked(false);
            userRefresh.setExpiredDate(Instant.now().plusSeconds(refreshTokenTtl));
            usecase.create(userRefresh);
            return userRefresh.getToken();
        } catch (NoSuchAlgorithmException _) {
            throw new MetierException("REFRESH_HASH_ERROR");
        }
    }

    @Transactional
    public void deleteUserRefresh(String userToken) {
        usecase.deleteUserRefreshByToken(userToken);
    }

    public User findUserByRefreshToken(String refreshToken) {
        var userToken = usecase.findByToken(refreshToken);
        if (userToken == null) {
            throw new MetierException(REFRESH_TOKEN_ERROR);
        }
        return userToken.getUser();
    }

    @Transactional
    public Long deleteExpiredTokens() {
        return usecase.deleteUserRefreshWhenExpired();
    }
}
