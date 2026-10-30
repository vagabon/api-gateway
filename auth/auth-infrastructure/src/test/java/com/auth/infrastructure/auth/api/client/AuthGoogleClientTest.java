package com.auth.infrastructure.auth.api.client;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthGoogleClientTest {

    private static final String BASE_URL = "https://oauth2.test";
    private static final String CLIENT_ID = "client-id";

    private MockRestServiceServer server;
    private AuthGoogleClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AuthGoogleClient(builder, BASE_URL, CLIENT_ID);
    }

    @Test
    void googleOauth2_shouldReturnAuthGoogle_whenTokenIsValid() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(
                header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(content().string("id_token=valid-token"))
            .andRespond(
                withSuccess(tokenInfo(CLIENT_ID, "true"), MediaType.APPLICATION_JSON));

        var result = client.googleOauth2("valid-token");

        assertThat(result.sub()).isEqualTo("123");
        assertThat(result.aud()).isEqualTo(CLIENT_ID);
        assertThat(result.email()).isEqualTo("vagabond@example.com");
        assertThat(result.emailVerified()).isTrue();
        assertThat(result.givenName()).isEqualTo("Vagabond");
        assertThat(result.picture()).isEqualTo("https://example.com/p.png");
        server.verify();
    }

    @Test
    void googleOauth2_shouldThrowUnauthorized_whenAudienceDoesNotMatch() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withSuccess(tokenInfo("other-client", "true"),
                MediaType.APPLICATION_JSON));

        assertUnauthorized("t");
    }

    @Test
    void googleOauth2_shouldThrowUnauthorized_whenEmailNotVerified() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(
                withSuccess(tokenInfo(CLIENT_ID, "false"), MediaType.APPLICATION_JSON));

        assertUnauthorized("t");
    }

    @Test
    void googleOauth2_shouldThrowUnauthorized_whenBodyIsEmpty() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withSuccess());

        assertUnauthorized("t");
    }

    @Test
    void googleOauth2_shouldThrowUnauthorized_whenGoogleReturns400() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertUnauthorized("bad-token");
    }

    @Test
    void googleOauth2_shouldThrowUnauthorized_whenGoogleReturns5xx() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withServerError());

        assertUnauthorized("any");
    }

    @Test
    void googleOauth2_shouldPropagateResourceAccessException_whenNetworkFails() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(request -> {
                throw new IOException("boom");
            });

        assertThatThrownBy(() -> client.googleOauth2("any"))
            .isInstanceOf(ResourceAccessException.class);
    }

    // ---------- helpers ----------

    private void assertUnauthorized(String idToken) {
        assertThatThrownBy(() -> client.googleOauth2(idToken))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> {
                var e = (ResponseStatusException) ex;
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                assertThat(e.getReason()).isEqualTo("Invalid Google token");
            });
    }

    private static String tokenInfo(String aud, String emailVerified) {
        return """
            {
              "iss": "https://accounts.google.com",
              "aud": "%s",
              "sub": "123",
              "email": "vagabond@example.com",
              "email_verified": "%s",
              "name": "Vagabond Test",
              "picture": "https://example.com/p.png",
              "given_name": "Vagabond",
              "family_name": "Test"
            }
            """.formatted(aud, emailVerified);
    }
}