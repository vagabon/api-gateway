package com.auth.infrastructure.user.user.adapter;

import java.util.stream.Collectors;

import com.auth.infrastructure.user.user.redis.RoleCache;
import com.auth.infrastructure.user.user.redis.UserCache;
import com.base.domain.exception.MetierException;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRedisAdapter {

    private final UserAdapter userAdapter;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public UserCache getUser(Long id) {
        try {
            var user = userAdapter.findById(id);
            var roles = user.getRoles().stream().map(
                role -> new RoleCache(role.getName(), role.getRoleName(),
                    role.getOrigin()))
                .collect(Collectors.toSet());
            return new UserCache(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles);
        } catch (MetierException _) {
            return null;
        }
    }

    @CacheEvict(cacheNames = "users", key = "#id")
    public void evict(Long id) {
        var cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.evict(id);
        }
    }
}