package com.base.infrastructure.jpa.entity;

import java.time.Instant;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class BaseCrudJpaEntity extends BaseIdJpaEntity {

    private Boolean active;

    private Instant creationDate;
    private Instant updatedDate;
    private Instant deletedDate;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.creationDate == null) {
            this.creationDate = Instant.now();
        }
        this.updatedDate = Instant.now();
        if (this.active == null) {
            this.active = true;
        }
    }
}
