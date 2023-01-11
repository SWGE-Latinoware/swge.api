package celtab.swge.controller;

import celtab.swge.dto.CaravanEnrollmentRequestDTO;
import celtab.swge.dto.CaravanRequestDTO;
import celtab.swge.dto.UserRequestDTO;
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
},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanEnrollmentControllerTest extends GenericTestController {

    private CaravanEnrollmentRequestDTO caravanEnrollmentRequestDTO;

    public CaravanEnrollmentControllerTest() {
        baseURL = "/api/caravan-enrollments";
    }

    @BeforeEach
    private void init() {
        caravanEnrollmentRequestDTO = new CaravanEnrollmentRequestDTO();
        var caravanRequestDTO = new CaravanRequestDTO();
        var userRequestDTO = new UserRequestDTO();
        caravanEnrollmentRequestDTO.setAccepted(false);
        caravanEnrollmentRequestDTO.setPayed(true);
        caravanEnrollmentRequestDTO.setConfirmed(false);
        caravanRequestDTO.setId(3L);
        caravanEnrollmentRequestDTO.setCaravan(caravanRequestDTO);
        userRequestDTO.setId(2L);
        caravanEnrollmentRequestDTO.setUser(userRequestDTO);
    }

    @Test
    void updateShouldReturnOk() {
        caravanEnrollmentRequestDTO.setId(2L);
        updateShouldReturnStatus(caravanEnrollmentRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        caravanEnrollmentRequestDTO.setId(54L);
        updateShouldReturnStatus(caravanEnrollmentRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
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
                Map.of("ids", List.of("1", "12"))
            ),
            HttpStatus.CONFLICT
        );
    }
    */

    @Test
    void findAllByCaravanShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL + "/caravan/1", HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(1);
    }

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
    void verifyCaravanNotPayShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            baseURL + "/verify-enrollment/edition/1/user/2",
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void verifyCaravanNotPayShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            baseURL + "/verify-enrollment/edition/2/user/2",
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }
}
