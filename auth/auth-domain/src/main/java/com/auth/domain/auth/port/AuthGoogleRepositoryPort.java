package com.auth.domain.auth.port;

import com.auth.domain.auth.model.AuthGoogle;

public interface AuthGoogleRepositoryPort {
    AuthGoogle googleOauth2(String idToken);
}
