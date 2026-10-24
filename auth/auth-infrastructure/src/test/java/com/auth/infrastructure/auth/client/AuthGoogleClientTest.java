package com.auth.infrastructure.auth.client;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthGoogleClientTest {

    private static final String BASE_URL = "https://oauth2.googleapis.com";

    private MockRestServiceServer server;
    private AuthGoogleClient client;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AuthGoogleClient(builder, BASE_URL);
    }

    @Test
    void verifyIdToken_sendsFormPostAndMapsResponse() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(content().string("id_token=my-id-token"))
            .andRespond(withSuccess("""
                {
                  "aud": "my-client-id",
                  "sub": "sub-1",
                  "email": "gonz@mail.com",
                  "email_verified": "true",
                  "given_name": "Gonz",
                  "picture": "http://pic"
                }
                """, MediaType.APPLICATION_JSON));

        var result = client.verifyIdToken("my-id-token");

        assertThat(result.aud()).isEqualTo("my-client-id");
        assertThat(result.sub()).isEqualTo("sub-1");
        assertThat(result.email()).isEqualTo("gonz@mail.com");
        assertThat(result.emailVerified()).isTrue();
        assertThat(result.givenName()).isEqualTo("Gonz");
        assertThat(result.picture()).isEqualTo("http://pic");
        server.verify();
    }

    @Test
    void verifyIdToken_urlEncodesToken() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andExpect(content().string("id_token=a%2Bb%3Dc"))
            .andRespond(withSuccess("""
                {
                  "aud": "my-client-id",
                  "sub": "sub-1",
                  "email": "gonz@mail.com",
                  "email_verified": "true",
                  "given_name": "Gonz",
                  "picture": "http://pic"
                }
                """, MediaType.APPLICATION_JSON));

        client.verifyIdToken("a+b=c");

        server.verify();
    }

    @Test
    void verifyIdToken_invalidToken_throwsRestClientResponseException() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withBadRequest().body("""
                {"error": "invalid_token"}
                """).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.verifyIdToken("bad"))
            .isInstanceOfSatisfying(RestClientResponseException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void verifyIdToken_googleServerError_throwsRestClientResponseException() {
        server.expect(requestTo(BASE_URL + "/tokeninfo"))
            .andRespond(withServerError());

        assertThatThrownBy(() -> client.verifyIdToken("any"))
            .isInstanceOf(RestClientResponseException.class);
    }
}