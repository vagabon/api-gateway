package com.base.domain.model;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class BaseCrudDomain extends BaseIdDomain {
    private Boolean active;
    private Instant creationDate;
    private Instant updatedDate;
    private Instant deletedDate;
}
