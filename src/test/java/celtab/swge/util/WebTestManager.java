package celtab.swge.util;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

public class WebTestManager {

    private final WebTestClient webTestClient;

    private final JWTTokenProvider jwtTokenProvider;

    private Object uri;

    private String jwtToken;

    public WebTestManager(WebTestClient webTestClient, JWTTokenProvider jwtTokenProvider) {
        this.webTestClient = webTestClient;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public WebTestManager uri(String uri) {
        this.uri = uri;
        return this;
    }

    public WebTestManager uri(Object uri) {
        this.uri = uri;
        return this;
    }

    public WebTestManager uri(URIObject uri) {
        this.uri = uri;
        return this;
    }

    private URI getURI(UriBuilder uriBuilder) {
        if (String.class.isAssignableFrom(uri.getClass())) {
            return uriBuilder.path((String) uri).build();
        }
        if (URI.class.isAssignableFrom(uri.getClass())) {
            return (URI) uri;
        }
        var obj = (URIObject) uri;
        var builder = uriBuilder
            .path(obj.getUrl());
        obj.getAttributes().forEach(builder::queryParam);
        return builder.build();
    }

    public WebTestClient.RequestHeadersSpec<?> get() {
        return webTestClient
            .get()
            .uri(this::getURI)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(jwtToken));
    }

    public WebTestClient.RequestBodySpec post() {
        return webTestClient
            .post()
            .uri(this::getURI)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(jwtToken));
    }

    public WebTestClient.RequestBodySpec put() {
        return webTestClient
            .put()
            .uri(this::getURI)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(jwtToken));
    }

    public WebTestClient.RequestHeadersSpec<?> delete() {
        return webTestClient
            .delete()
            .uri(this::getURI)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(jwtToken));
    }

    public WebTestManager authentication(String email, String password, Boolean rememberMe) {
        jwtToken = jwtTokenProvider.createToken(email, password, rememberMe);
        return this;
    }

    public WebTestManager authenticationDefaultAdmin() {
        return authentication("admin@gmail.teste.com", "pti123", false);
    }

    public WebTestManager authenticationOrDefaultAdmin() {
        if (jwtToken == null) {
            return authenticationDefaultAdmin();
        }
        return this;
    }
}
