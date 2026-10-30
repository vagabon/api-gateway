package com.auth.infrastructure.user.cache.service;

import java.util.stream.Collectors;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.user.cache.dto.UserCache;
import com.auth.infrastructure.user.user.adapter.UserAdapter;
import com.base.domain.exception.MetierException;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserAdapter userAdapter;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    @SuppressWarnings("null")
    public UserCache getUser(Long id) {
        try {
            var user = userAdapter.findById(id);
            return new UserCache(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfiles().stream().map(Profile::getName)
                    .collect(Collectors.toSet()));
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