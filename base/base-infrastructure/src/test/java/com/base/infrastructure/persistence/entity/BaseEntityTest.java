package com.base.infrastructure.persistence.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    @Test
    void prePersist_renseigneLesValeursParDefaut() {
        BaseEntity entity = new BaseEntity();
        Instant start = Instant.now();

        entity.prePersist();

        assertThat(entity.getCreationDate()).isAfterOrEqualTo(start);
        assertThat(entity.getUpdatedDate()).isAfterOrEqualTo(start);
        assertThat(entity.getActive()).isTrue();
        assertThat(entity.getDeletedDate()).isNull();
    }

    @Test
    void prePersist_neEcrasePasCreationDateExistante() {
        BaseEntity entity = new BaseEntity();
        Instant creation = Instant.now().minus(10, ChronoUnit.DAYS);
        entity.setCreationDate(creation);

        entity.prePersist();

        assertThat(entity.getCreationDate()).isEqualTo(creation);
    }

    @Test
    void prePersist_metToujoursAJourUpdatedDate() {
        BaseEntity entity = new BaseEntity();
        Instant old = Instant.now().minus(1, ChronoUnit.DAYS);
        entity.setUpdatedDate(old);

        entity.prePersist();

        assertThat(entity.getUpdatedDate()).isAfter(old);
    }

    @Test
    void prePersist_conserveActiveFalse() {
        BaseEntity entity = new BaseEntity();
        entity.setActive(false);

        entity.prePersist();

        assertThat(entity.getActive()).isFalse();
    }

    @Test
    void prePersist_conserveActiveTrue() {
        BaseEntity entity = new BaseEntity();
        entity.setActive(true);

        entity.prePersist();

        assertThat(entity.getActive()).isTrue();
    }
}