package celtab.swge.controller;

import celtab.swge.dto.PromotionRequestDTO;
import celtab.swge.dto.RegistrationTypeRequestDTO;
import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RegistrationTypeControllerTest extends GenericTestController {

    private RegistrationTypeRequestDTO registrationTypeRequestDTO;

    public RegistrationTypeControllerTest() {
        baseURL = "/api/registration-types";
    }

    @BeforeEach
    private void init() {
        registrationTypeRequestDTO = new RegistrationTypeRequestDTO();
        registrationTypeRequestDTO.setPrice(100.0);
        registrationTypeRequestDTO.setInitialDateTime(new Date());
        registrationTypeRequestDTO.setFinalDateTime(Date.from(LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));
        registrationTypeRequestDTO.setPromotions(Collections.emptyList());
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(registrationTypeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        registrationTypeRequestDTO.setPrice(null);
        createShouldReturnStatus(registrationTypeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestNotValidPromotion() {
        var promo = new PromotionRequestDTO();

        promo.setFinalDateTime(Date.from(Instant.now().plus(Period.ofDays(10))));
        promo.setInitialDateTime(Date.from(Instant.now()));
        promo.setPercentage(99d);

        registrationTypeRequestDTO.setPromotions(List.of(promo));

        createShouldReturnStatus(registrationTypeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        registrationTypeRequestDTO.setId(1L);
        updateShouldReturnStatus(registrationTypeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        registrationTypeRequestDTO.setId(21L);
        updateShouldReturnStatus(registrationTypeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnUserPermission() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatusAndBody(baseURL + "/21", HttpStatus.NOT_FOUND);
    }
}

