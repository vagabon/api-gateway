package com.auth.domain.user.model;

import java.time.Instant;
import java.util.List;

import com.auth.domain.profile.model.Profile;
import com.base.domain.model.BaseDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class User extends BaseDomain {
    String username;
    String email;
    String avatar;
    String googleId;
    Instant lastConnexionDate;
    List<Profile> profiles;
}