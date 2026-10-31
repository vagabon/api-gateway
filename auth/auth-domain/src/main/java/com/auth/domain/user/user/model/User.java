package com.auth.domain.user.user.model;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.auth.domain.role.model.Role;
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
    Set<Role> roles;

    public void updateUserAvatar(String avatar) {
        setAvatar(avatar);
        updateLastConnexion();
    }

    public void updateLastConnexion() {
        setLastConnexionDate(Instant.now());
    }

    public Set<Role> getRolesFromOrigin(String origin) {
        return roles.stream().filter(role -> role.getOrigin().equals(origin))
            .collect(Collectors.toSet());
    }
}