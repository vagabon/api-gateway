package com.auth.infrastructure.auth.client;

import com.auth.infrastructure.auth.dto.response.google.AuthGoogleHttpResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class AuthGoogleClient {

    private final RestClient restClient = RestClient.builder()
        .baseUrl("https://oauth2.googleapis.com")
        .build();

    public AuthGoogleHttpResponse verifyIdToken(String idToken) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("id_token", idToken);

        return restClient.post()
            .uri("/tokeninfo")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(AuthGoogleHttpResponse.class);
    }
}
