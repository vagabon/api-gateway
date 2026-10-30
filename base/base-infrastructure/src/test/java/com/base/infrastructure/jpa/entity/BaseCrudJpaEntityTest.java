package com.base.infrastructure.jpa.entity;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseCrudJpaEntityTest {

    private static BaseCrudJpaEntity newEntity() {
        return new BaseCrudJpaEntity() {
        };
    }

    @Test
    void prePersist_shouldInitializeDates_andActive_onNewEntity() {
        var entity = newEntity();
        var before = Instant.now();

        entity.prePersist();

        assertThat(entity.getCreationDate()).isBetween(before, Instant.now());
        assertThat(entity.getUpdatedDate()).isBetween(before, Instant.now());
        assertThat(entity.getActive()).isTrue();
        assertThat(entity.getDeletedDate()).isNull();
    }

    @Test
    void prePersist_shouldKeepExistingCreationDate() {
        var entity = newEntity();
        entity.setCreationDate(Instant.EPOCH);

        entity.prePersist();

        assertThat(entity.getCreationDate()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void prePersist_shouldRefreshUpdatedDate_onEachCall() {
        var entity = newEntity();
        entity.setCreationDate(Instant.EPOCH);
        entity.setUpdatedDate(Instant.EPOCH);

        entity.prePersist();

        assertThat(entity.getUpdatedDate()).isAfter(Instant.EPOCH);
    }

    @Test
    void prePersist_shouldKeepActiveFalse_whenExplicitlyDeactivated() {
        var entity = newEntity();
        entity.setActive(false);

        entity.prePersist();

        assertThat(entity.getActive()).isFalse();
    }

    @Test
    void prePersist_shouldNotTouchDeletedDate() {
        var entity = newEntity();
        var deleted = Instant.parse("2026-01-01T00:00:00Z");
        entity.setDeletedDate(deleted);

        entity.prePersist();

        assertThat(entity.getDeletedDate()).isEqualTo(deleted);
    }
}