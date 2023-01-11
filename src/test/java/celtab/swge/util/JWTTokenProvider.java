package celtab.swge.util;

import celtab.swge.auth.JWTUser;
import org.springframework.test.web.reactive.server.WebTestClient;

public class JWTTokenProvider {

    private final WebTestClient webTestClient;

    public JWTTokenProvider(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public String createToken(String email, String password, Boolean rememberMe) {
        return webTestClient
            .post()
            .uri("/api/login")
            .bodyValue(new JWTUser(email, password, rememberMe))
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class)
            .getResponseBody()
            .blockFirst();
    }

}
