package com.auth.domain.profile.model;

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
public class Profile extends BaseCrudDomain {
    private String name;
    private String roles;

    private Instant endPlan;
}