package com.base.domain.model;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class BaseDomain {
    private Long id;
    private Boolean active;
    private Instant creationDate;
    private Instant updatedDate;
    private Instant deletedDate;
}
