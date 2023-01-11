package celtab.swge.controller;

import celtab.swge.dto.CaravanRequestDTO;
import celtab.swge.dto.CaravanTutoredEnrollmentRequestDTO;
import celtab.swge.dto.TutoredUserRequestDTO;
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
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/caravan_data.sql",
    "classpath:db.scripts/caravan_enrollment_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/caravan_tutored_enrollment_data.sql"
},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanTutoredEnrollmentControllerTest extends GenericTestController {

    private CaravanTutoredEnrollmentRequestDTO caravanTutoredEnrollmentRequestDTO;

    public CaravanTutoredEnrollmentControllerTest() {
        baseURL = "/api/caravan-tutored-enrollments";
    }

    @BeforeEach
    private void init() {
        var caravanRequestDTO = new CaravanRequestDTO();
        var tutoredRequestDTO = new TutoredUserRequestDTO();
        caravanTutoredEnrollmentRequestDTO = new CaravanTutoredEnrollmentRequestDTO();
        caravanTutoredEnrollmentRequestDTO.setAccepted(true);
        caravanTutoredEnrollmentRequestDTO.setPayed(false);
        caravanRequestDTO.setId(1L);
        caravanTutoredEnrollmentRequestDTO.setCaravan(caravanRequestDTO);
        tutoredRequestDTO.setId(1L);
        caravanTutoredEnrollmentRequestDTO.setTutoredUser(tutoredRequestDTO);
    }

    @Test
    void updateShouldReturnOk() {
        caravanTutoredEnrollmentRequestDTO.setId(1L);
        updateShouldReturnStatus(caravanTutoredEnrollmentRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        caravanTutoredEnrollmentRequestDTO.setId(42L);
        updateShouldReturnStatus(caravanTutoredEnrollmentRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "2"))
            ),
            HttpStatus.OK
        );
    }

    /*
    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "101"))
            ),
            HttpStatus.CONFLICT
        );
    }
     */

    @Test
    void findAllByIdShouldReturnOk() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/list/ids",
                Map.of("ids", List.of("1", "2"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void findAllByCaravanShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL + "/caravan/1", HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(1);
    }
}
