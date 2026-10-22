package com.base.infrastructure.persistence.entity;

import java.time.Instant;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@EqualsAndHashCode
@ToString
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
