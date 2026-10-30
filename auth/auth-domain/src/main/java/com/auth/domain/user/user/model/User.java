package com.auth.domain.user.user.model;

import java.time.Instant;
import java.util.Set;

import com.auth.domain.profile.model.Profile;
import com.base.domain.model.BaseCrudDomain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class User extends BaseCrudDomain {
    String username;
    String email;
    String avatar;
    String googleId;
    Instant lastConnexionDate;
    Set<Profile> profiles;

    public void updateUserAvatar(String avatar) {
        setAvatar(avatar);
        updateLastConnexion();
    }

    public void updateLastConnexion() {
        setLastConnexionDate(Instant.now());
    }
}