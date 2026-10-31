package com.auth.domain.role.model;

import java.time.Instant;

import com.base.domain.model.BaseCrudDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseCrudDomain {
    private String name;
    private String roleName;
    private String origin;

    private Instant endPlan;
}