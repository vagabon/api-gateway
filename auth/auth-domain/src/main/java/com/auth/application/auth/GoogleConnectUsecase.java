package com.auth.application.auth;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.port.AuthGoogleRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleConnectUsecase {

    private final AuthGoogleRepositoryPort authGoogleRepository;

    public AuthGoogle execute(String idToken) {
        return authGoogleRepository.googleOauth2(idToken);
    }

}
