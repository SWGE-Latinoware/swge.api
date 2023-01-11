package celtab.swge.controller;


import celtab.swge.dto.*;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/tutored_registration_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TutoredRegistrationControllerTest extends GenericTestController {

    private TutoredRegistrationRequestDTO tutoredRegistrationRequestDTO;

    public TutoredRegistrationControllerTest() {
        baseURL = "/api/tutored-registrations";
    }

    @BeforeEach
    private void init() {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        var userRequestDTO = new TutoredUserRequestDTO();
        userRequestDTO.setId(2L);
        var activity = new ActivityRequestDTO();
        activity.setId(2L);
        var individualRegistration = new IndividualRegistrationRequestDTO();
        individualRegistration.setActivity(activity);
        tutoredRegistrationRequestDTO = new TutoredRegistrationRequestDTO();
        tutoredRegistrationRequestDTO.setTutoredUser(userRequestDTO);
        tutoredRegistrationRequestDTO.setEdition(editionRequestDTO);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "22"))), HttpStatus.CONFLICT);
    }

    @Test
    void filterShouldReturnRegistration() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("edition.id"));
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(filter)))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void findOneShouldReturnRegistration() {
        findShouldReturnStatusAndBody(
            baseURL + "/edition/2/registration/1",
            HttpStatus.OK
        ).jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(
            baseURL + "/edition/2/registration/2",
            HttpStatus.NOT_FOUND
        );
    }
}
