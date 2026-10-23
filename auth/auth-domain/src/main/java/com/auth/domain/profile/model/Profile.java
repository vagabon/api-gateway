package com.auth.domain.profile.model;

import java.time.Instant;

import com.base.domain.model.BaseDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Profile extends BaseDomain {
    private String name;
    private String roles;

    private Instant endPlan;
}