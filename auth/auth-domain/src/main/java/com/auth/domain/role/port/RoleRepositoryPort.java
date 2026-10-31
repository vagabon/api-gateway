package com.auth.domain.role.port;

import com.auth.domain.role.model.Role;

public interface RoleRepositoryPort {
    Role getRoleForUser(String origin);
}
