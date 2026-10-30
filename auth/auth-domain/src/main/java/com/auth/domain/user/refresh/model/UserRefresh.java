package com.auth.domain.user.refresh.model;

import java.time.Instant;

import com.auth.domain.user.user.model.User;
import com.base.domain.model.BaseCrudDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserRefresh extends BaseCrudDomain {

    private String token;

    private String ipAddress;

    private Boolean revoked;

    private Instant expiredDate;

    private User user;

}
