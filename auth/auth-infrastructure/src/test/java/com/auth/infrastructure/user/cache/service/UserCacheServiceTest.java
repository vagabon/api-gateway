package com.auth.infrastructure.user.cache.service;

import java.util.List;
import java.util.stream.Collectors;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.cache.dto.UserCache;
import com.auth.infrastructure.user.user.adapter.UserAdapter;
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
    private UserCacheService service;

    @Test
    void getUser_mapsUserToCacheUser() {
        var admin = mock(Profile.class);
        var user1 = mock(Profile.class);
        when(admin.getName()).thenReturn("ADMIN");
        when(user1.getName()).thenReturn("USER");

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("vagabond");
        when(user.getEmail()).thenReturn("vagabond@mail.com");
        when(user.getProfiles())
            .thenReturn(List.of(admin, user1).stream().collect(Collectors.toSet()));
        when(userAdapter.findById(1L)).thenReturn(user);

        var result = service.getUser(1L);

        assertThat(result).isEqualTo(
            new UserCache(1L, "vagabond", "vagabond@mail.com",
                List.of("ADMIN", "USER").stream().collect(Collectors.toSet())));
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