package celtab.swge.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@AutoConfigureWebTestClient(timeout = "PT10M")
public abstract class GenericTestController extends GenericTest {

    @Autowired
    protected WebTestClient webTestClient;

    private WebTestManager webTestManager;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String baseURL;

    protected WebTestManager getWebTestManager() {
        if (webTestManager == null) {
            webTestManager = new WebTestManager(webTestClient, new JWTTokenProvider(webTestClient));
        }
        return webTestManager;
    }

    protected String getURLEncodedValue(Object value) {
        try {
            var str = objectMapper.writeValueAsString(value);
            return URLEncoder.encode(str, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected void authenticate(String email, String password, Boolean rememberMe) {
        getWebTestManager().authentication(email, password, rememberMe);
    }

    protected void createShouldReturnStatus(Object request, Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .post()
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected void createMultiPartShouldReturnStatus(BodyInserters.MultipartInserter request, Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .post()
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(request)
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected void updateShouldReturnStatus(Object request, Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .put()
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected void updateMultiPartShouldReturnStatus(BodyInserters.MultipartInserter request, Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .put()
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(request)
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected void deleteShouldReturnStatus(Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .delete()
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected void findShouldReturnStatus(Object uri, HttpStatus status) {
        getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .get()
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    protected WebTestClient.BodyContentSpec findShouldReturnStatusAndBody(Object uri, HttpStatus status) {
        return getWebTestManager()
            .uri(uri)
            .authenticationOrDefaultAdmin()
            .get()
            .exchange()
            .expectStatus().isEqualTo(status)
            .expectBody();
    }

}
