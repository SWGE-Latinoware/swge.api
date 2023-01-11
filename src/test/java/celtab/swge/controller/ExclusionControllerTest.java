package celtab.swge.controller;


import celtab.swge.dto.*;
import celtab.swge.model.enums.ExclusionStatus;
import celtab.swge.model.enums.RequestType;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/delete_request_data.sql",
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/user_permission_data.sql",
    "classpath:db.scripts/exclusion_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ExclusionControllerTest extends GenericTestController {

    private ExclusionRequestDTO exclusionRequestDTO;

    public ExclusionControllerTest() {
        baseURL = "/api/exclusions";
    }

    @BeforeEach
    private void init() {
        var dpo = new UserRequestDTO();
        dpo.setId(3L);
        dpo.setEmail("admin@gmail.teste.com");
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);
        userRequestDTO.setEmail("usuario@gmail.teste.com");
        var deleteRequestDTO = new DeleteRequestRequestDTO();
        deleteRequestDTO.setRequestDate(Date.from(Instant.now()));
        deleteRequestDTO.setRequestType(RequestType.INACTIVITY);
        exclusionRequestDTO = new ExclusionRequestDTO();
        exclusionRequestDTO.setStatus(ExclusionStatus.NOT_ANALYZED);
        exclusionRequestDTO.setDeleteRequest(deleteRequestDTO);
        exclusionRequestDTO.setUser(userRequestDTO);
        exclusionRequestDTO.setDpo(dpo);
        exclusionRequestDTO.setRegistryDate(Date.from(Instant.now()));
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(exclusionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        exclusionRequestDTO.setRegistryDate(null);
        createShouldReturnStatus(exclusionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Test");
        exclusionRequestDTO.setDeadlineExclusionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        updateShouldReturnStatus(exclusionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequestNoDeadlineExclusionDate() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Test");
        exclusionRequestDTO.setDeadlineExclusionDate(null);
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        updateShouldReturnStatus(exclusionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnExclusion() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void deleteAllShouldAlwaysReturnInternetServerError() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "2"))
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    void deleteShouldAlwaysReturnInternetServerError() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void filterShouldReturnExclusion() {
        var filter = new GenericFilterDTO();
        filter.setQuery("1");
        filter.setQueryFields(List.of("status"));
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
    void findAllShouldReturnPage() {
        findShouldReturnStatus(baseURL, HttpStatus.OK);
    }

    @Test
    void concludeUserShouldReturnOk() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        deleteRequest.setRequestDate(Date.from(Instant.now()));
        deleteRequest.setRequestType(RequestType.INACTIVITY);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Exclusão Aprovada");
        exclusionRequestDTO.setDeadlineExclusionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        exclusionRequestDTO.setEffectiveDeletionDate(Date.from(Instant.now()));

        createShouldReturnStatus(exclusionRequestDTO, baseURL + "/conclude", HttpStatus.OK);
    }

    @Test
    void concludeTutoredUserShouldReturnOk() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        deleteRequest.setRequestDate(Date.from(Instant.now()));
        deleteRequest.setRequestType(RequestType.TUTORED);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Exclusão Aprovada");
        exclusionRequestDTO.setDeadlineExclusionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        exclusionRequestDTO.setEffectiveDeletionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setUser(null);

        var tutoredUserRequestDTO = new TutoredUserRequestDTO();
        tutoredUserRequestDTO.setId(1L);

        exclusionRequestDTO.setTutoredUser(tutoredUserRequestDTO);

        createShouldReturnStatus(exclusionRequestDTO, baseURL + "/conclude", HttpStatus.OK);
    }

    @Test
    void concludeUserShouldReturnBadRequestNoEffectiveDeletionDate() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Exclusão Aprovada");
        exclusionRequestDTO.setDeadlineExclusionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        exclusionRequestDTO.setEffectiveDeletionDate(null);
        createShouldReturnStatus(exclusionRequestDTO, baseURL + "/conclude", HttpStatus.BAD_REQUEST);
    }

    @Test
    void concludeUserShouldReturnBadRequestNoUserOrTutoredUser() {
        var deleteRequest = new DeleteRequestRequestDTO();
        deleteRequest.setId(1L);
        exclusionRequestDTO.setDeleteRequest(deleteRequest);
        exclusionRequestDTO.setId(1L);
        exclusionRequestDTO.setNote("Exclusão Aprovada");
        exclusionRequestDTO.setDeadlineExclusionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setReturnDate(Date.from(Instant.now()));
        exclusionRequestDTO.setStatus(ExclusionStatus.APPROVED);
        exclusionRequestDTO.setEffectiveDeletionDate(Date.from(Instant.now()));
        exclusionRequestDTO.setUser(null);
        createShouldReturnStatus(exclusionRequestDTO, baseURL + "/conclude", HttpStatus.BAD_REQUEST);
    }
}
