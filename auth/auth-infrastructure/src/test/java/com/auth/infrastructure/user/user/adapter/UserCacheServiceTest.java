package com.auth.infrastructure.user.user.adapter;

import java.util.Set;

import com.auth.domain.role.model.Role;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.user.redis.RoleCache;
import com.auth.infrastructure.user.user.redis.UserCache;
import com.base.domain.exception.MetierException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCacheServiceTest {

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private UserRedisAdapter service;

    @Test
    void getUser_mapsUserToCacheUser() {
        var admin = mock(Role.class);
        var user1 = mock(Role.class);
        when(admin.getName()).thenReturn("ADMIN_BLOG");
        when(admin.getRoleName()).thenReturn("ADMIN");
        when(admin.getOrigin()).thenReturn("blog");
        when(user1.getName()).thenReturn("USER_BLOG");
        when(user1.getRoleName()).thenReturn("USER");
        when(user1.getOrigin()).thenReturn("blog");

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("vagabond");
        when(user.getEmail()).thenReturn("vagabond@mail.com");
        when(user.getRoles())
            .thenReturn(Set.of(admin, user1));
        when(userAdapter.findById(1L)).thenReturn(user);

        var result = service.getUser(1L);

        var roles = Set.of(new RoleCache("ADMIN_BLOG", "ADMIN", "blog"),
            new RoleCache("USER_BLOG", "USER", "blog"));
        assertThat(result).isEqualTo(
            new UserCache(1L, "vagabond", "vagabond@mail.com",
                roles));
    }

    @Test
    void getUser_returnsNullWhenUserNotFound() {
        when(userAdapter.findById(99L)).thenThrow(new MetierException("USER_NOT_FOUND"));

        assertThat(service.getUser(99L)).isNull();
    }

    @Test
    void evict_evictsFromUsersCache() {
        when(cacheManager.getCache("users")).thenReturn(cache);

        service.evict(1L);

        verify(cache).evict(1L);
    }

    @Test
    void evict_doesNothingWhenCacheMissing() {
        when(cacheManager.getCache("users")).thenReturn(null);

        assertThatCode(() -> service.evict(1L)).doesNotThrowAnyException();
    }
}