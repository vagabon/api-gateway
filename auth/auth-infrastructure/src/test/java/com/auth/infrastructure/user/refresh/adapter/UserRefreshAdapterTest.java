package com.auth.infrastructure.user.refresh.adapter;

import java.time.Instant;
import java.util.Optional;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.infrastructure.user.refresh.mapper.UserRefreshMapper;
import com.auth.infrastructure.user.refresh.persistence.entity.UserRefreshEntity;
import com.auth.infrastructure.user.refresh.persistence.repository.UserRefreshRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRefreshAdapterTest {

    @Mock
    private UserRefreshRepository repository;

    @Mock
    private UserRefreshMapper mapper;

    @InjectMocks
    private UserRefreshAdapter adapter;

    @Captor
    private ArgumentCaptor<Instant> instantCaptor;

    @Test
    void findByToken_returnsMappedDomainWhenFound() {
        var entity = new UserRefreshEntity();
        var domain = new UserRefresh();
        when(repository.findByTokenAndRevokedFalseAndExpiredDateAfter(
            eq("abc"), any(Instant.class))).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.findByToken("abc")).isSameAs(domain);
    }

    @Test
    void findByToken_passesCurrentInstantToRepository() {
        var before = Instant.now();
        when(repository.findByTokenAndRevokedFalseAndExpiredDateAfter(
            eq("abc"), any(Instant.class))).thenReturn(Optional.empty());

        adapter.findByToken("abc");

        var after = Instant.now();
        verify(repository).findByTokenAndRevokedFalseAndExpiredDateAfter(
            eq("abc"), instantCaptor.capture());
        assertThat(instantCaptor.getValue()).isBetween(before, after);
    }

    @Test
    void findByToken_returnsNullWhenNotFound() {
        when(repository.findByTokenAndRevokedFalseAndExpiredDateAfter(
            eq("unknown"), any(Instant.class))).thenReturn(Optional.empty());

        assertThat(adapter.findByToken("unknown")).isNull();
        verify(mapper).toDomain(null);
    }

    @Test
    void deleteUserRefreshByToken_delegatesToRepository() {
        when(repository.deleteByToken("abc")).thenReturn(1L);

        assertThat(adapter.deleteUserRefreshByToken("abc")).isEqualTo(1L);
        verify(repository).deleteByToken("abc");
    }

    @Test
    void deleteUserRefreshWhenExpired_usesCurrentInstant() {
        var before = Instant.now();
        when(repository.deleteByExpiredDateBefore(any(Instant.class))).thenReturn(5L);

        var deleted = adapter.deleteUserRefreshWhenExpired();

        var after = Instant.now();
        verify(repository).deleteByExpiredDateBefore(instantCaptor.capture());
        assertThat(deleted).isEqualTo(5L);
        assertThat(instantCaptor.getValue()).isBetween(before, after);
    }
}