package com.auth.domain.user.user.model;

import java.time.Instant;

import com.base.domain.model.BaseIdDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserLight extends BaseIdDomain {
    String username;
    String email;
    String avatar;
    Instant lastConnexionDate;
}
