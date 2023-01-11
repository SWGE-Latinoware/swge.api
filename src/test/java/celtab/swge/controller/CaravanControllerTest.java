package celtab.swge.controller;

import celtab.swge.dto.*;
import celtab.swge.model.enums.CaravanType;
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
    "classpath:db.scripts/user_permission_data.sql",
    "classpath:db.scripts/caravan_data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanControllerTest extends GenericTestController {

    private CaravanRequestDTO caravanRequestDTO;


    public CaravanControllerTest() {
        baseURL = "/api/caravans";
    }

    @BeforeEach
    private void init() {
        caravanRequestDTO = new CaravanRequestDTO();
        var institutionRequestDTO = new InstitutionRequestDTO();
        institutionRequestDTO.setId(1L);
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);

        caravanRequestDTO.setName("Caravana 5");
        caravanRequestDTO.setCountry("BR");
        caravanRequestDTO.setPayed(true);
        caravanRequestDTO.setPrice(200.00);
        caravanRequestDTO.setType(CaravanType.GENERAL);
        caravanRequestDTO.setVacancies(3);
        caravanRequestDTO.setCoordinator(userRequestDTO);
        caravanRequestDTO.setEdition(editionRequestDTO);
        caravanRequestDTO.setInstitution(institutionRequestDTO);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        caravanRequestDTO.setName(null);
        createShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createUserCoordinatorShouldReturnBadRequest() {
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(2L);
        caravanRequestDTO.setCoordinator(userRequestDTO);
        createShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        caravanRequestDTO.setId(1L);
        updateShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        caravanRequestDTO.setId(21L);
        updateShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateUserCoordinatorShouldReturnBadRequest() {
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(2L);
        caravanRequestDTO.setCoordinator(userRequestDTO);
        caravanRequestDTO.setId(1L);
        updateShouldReturnStatus(caravanRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteShouldReturnOK() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1", "3"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "7"))), HttpStatus.CONFLICT);
    }

    @Test
    void findAllShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL, HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(4);
    }

    @Test
    void findAllShouldReturnPage() {
        findShouldReturnStatus(baseURL, HttpStatus.OK);
    }

    @Test
    void filterShouldReturnCaravan() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Caravana 2");
        filter.setQueryFields(List.of("name"));
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
    void findOnShouldReturnCaravan() {
        findShouldReturnStatusAndBody(baseURL + "/2", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(2L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllShouldReturnEditionPage0() {
        findShouldReturnStatusAndBody(baseURL + "/edition/1", HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void findAllShouldReturnEditionPage() {
        findShouldReturnStatusAndBody(baseURL + "/edition/21", HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(0);
    }

    @Test
    void findAllShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/edition/1/enrollments/2", HttpStatus.OK);
    }

    @Test
    void caravanEnrollmentShouldReturnOk() {
        createShouldReturnStatus("", baseURL + "/enroll/2/3", HttpStatus.OK);
    }

    @Test
    void caravanEnrollmentShouldReturnBadRequest() {
        createShouldReturnStatus("", baseURL + "/enroll/3/3", HttpStatus.BAD_REQUEST);
    }

    @Test
    void caravanEnrollmentShouldReturnCaravanNotFound() {
        createShouldReturnStatus("", baseURL + "/enroll/9/3", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListShouldReturnOk() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/list/1", HttpStatus.OK);
    }

    @Test
    void caravanEnrollmentListShouldReturnBadRequest() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/list/3", HttpStatus.BAD_REQUEST);
    }

    @Test
    void caravanEnrollmentListShouldReturnNotFoundUser() {
        createShouldReturnStatus(List.of("4", "5"), baseURL + "/enroll/list/1", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListShouldReturnNotFoundOneUser() {
        createShouldReturnStatus(List.of("1", "5"), baseURL + "/enroll/list/1", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListShouldReturnNotFoundCaravan() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/list/9", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListShouldReturnConflict() {
        caravanEnrollmentListShouldReturnOk();
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/list/1", HttpStatus.CONFLICT);
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnOk() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/tutored/list/3", HttpStatus.OK);
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnBadRequest() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/tutored/list/1", HttpStatus.BAD_REQUEST);
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnNotFoundBothUsers() {
        createShouldReturnStatus(List.of("4", "5"), baseURL + "/enroll/tutored/list/3", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnNotFoundOneUser() {
        createShouldReturnStatus(List.of("1", "5"), baseURL + "/enroll/tutored/list/3", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnConflict() {
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/tutored/list/3", HttpStatus.OK);
        createShouldReturnStatus(List.of("1", "2"), baseURL + "/enroll/tutored/list/3", HttpStatus.CONFLICT);
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnOk() {
        var request = new TutoredUserRequestDTO();
        request.setName("Pedro Silva");
        request.setCountry("BR");
        request.setTagName("Pedro");
        request.setIdNumber("45454544545");
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/3", HttpStatus.OK);
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnBadRequest() {
        var request = new TutoredUserRequestDTO();
        request.setName("Pedro Silva");
        request.setCountry("BR");
        request.setTagName("Pedro");
        request.setIdNumber("45454544545");
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/1", HttpStatus.BAD_REQUEST);
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnConflictOnSave() {
        var request = new TutoredUserRequestDTO();
        request.setCountry("BR");
        request.setTagName("Pedro");
        request.setIdNumber("45454544545");
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/3", HttpStatus.CONFLICT);
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnNotFound() {
        var request = new TutoredUserRequestDTO();
        request.setId(3L);
        request.setName("Pedro Silva");
        request.setCountry("BR");
        request.setTagName("Pedro");
        request.setIdNumber("45454544545");
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/3", HttpStatus.NOT_FOUND);
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnConflict() {
        var request = new TutoredUserRequestDTO();
        request.setName("Pedro Silva");
        request.setCountry("BR");
        request.setTagName("Pedro");
        request.setIdNumber("45454544545");
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/3", HttpStatus.OK);
        createShouldReturnStatus(request, baseURL + "/enroll/tutored/3", HttpStatus.CONFLICT);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/edition/name",
                Map.of("name", List.of("Caravana 1"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/edition/name",
                Map.of("name", List.of("caravan 6000"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void findAllByListShouldReturnOk() {
        findShouldReturnStatus(
            baseURL + "/list",
            HttpStatus.OK
        );
    }
}
