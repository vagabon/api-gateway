package com.auth.domain.user.refresh.usercase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.in.IUserRefreshUsecasePort;
import com.auth.domain.user.refresh.port.out.IUserRefreshAdapterPort;
import com.auth.domain.user.user.model.User;
import com.base.domain.exception.MetierException;
import com.base.domain.usecase.BaseCrudUsecase;

public class UserRefreshUsecase
    extends BaseCrudUsecase<UserRefresh, IUserRefreshAdapterPort>
    implements IUserRefreshUsecasePort {

    public static final String ERROR_REFRESH_TOKEN = "NO_REFRESH_TOKEN";
    private static final String ENCRYPT_ALGORITHM = "SHA-256";

    public UserRefreshUsecase(
        IUserRefreshAdapterPort userRefreshAdapter) {
        super(userRefreshAdapter);
    }

    public String generateUserRefresh(User user, int ttl) {
        var userRefresh = new UserRefresh();
        userRefresh.setUser(user);
        var refreshToken = generateToken();
        userRefresh.setToken(refreshToken);
        userRefresh.setIpAddress("");
        userRefresh.setRevoked(false);
        userRefresh.setExpiredDate(Instant.now().plusSeconds(ttl));
        adapter.create(userRefresh);
        return userRefresh.getToken();
    }

    private static String generateToken() {
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

    @Override
    public UserRefresh findByToken(String token) {
        var userToken = adapter.findByToken(token);
        if (userToken == null) {
            throw new MetierException(ERROR_REFRESH_TOKEN);
        }
        return userToken;
    }

    @Override
    public long deleteByToken(String token) {
        return adapter.deleteUserRefreshByToken(token);
    }

    @Override
    public long deleteExpiredTokens() {
        return adapter.deleteUserRefreshWhenExpired();
    }
}
