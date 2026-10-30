package com.auth.infrastructure.auth.api.client;

import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.port.AuthGoogleRepositoryPort;
import com.auth.infrastructure.auth.api.response.AuthGoogleResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthGoogleClient
    implements AuthGoogleRepositoryPort {

    private final RestClient restClient;
    private final String googleClientId;

    public AuthGoogleClient(
        RestClient.Builder builder,
        @Value("${google.token-info-base-url:https://oauth2.googleapis.com}") String baseUrl,
        @Value("${google.client-id}") String googleClientId) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.googleClientId = googleClientId;
    }

    public AuthGoogle googleOauth2(String idToken) {
        try {
            var form = new LinkedMultiValueMap<String, String>();
            form.add("id_token", idToken);

            var authGoogleResponse = restClient.post()
                .uri("/tokeninfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(AuthGoogleResponse.class);

            if (authGoogleResponse == null
                || !googleClientId.equals(authGoogleResponse.aud())
                || !authGoogleResponse.emailVerified()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid Google token");
            }

            return new AuthGoogle(
                authGoogleResponse.sub(), authGoogleResponse.aud(),
                authGoogleResponse.email(), authGoogleResponse.emailVerified(),
                authGoogleResponse.givenName(), authGoogleResponse.picture());

        } catch (RestClientResponseException _) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Invalid Google token");
        }
    }
}
