package celtab.swge.controller;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.InstitutionRequestDTO;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InstitutionControllerTest extends GenericTestController {

    private InstitutionRequestDTO institutionRequestDTO;

    public InstitutionControllerTest() {
        baseURL = "/api/institutions";
    }

    @BeforeEach
    private void init() {
        institutionRequestDTO = new InstitutionRequestDTO();
        institutionRequestDTO.setName("Universidade das Cataratas");
        institutionRequestDTO.setShortName("UDC");
        institutionRequestDTO.setPhone("45998707070");
        institutionRequestDTO.setWebsite("www.udc.com.br");
        institutionRequestDTO.setCity("Foz do Iguacu");
        institutionRequestDTO.setState("PR");
        institutionRequestDTO.setCountry("BR");
        institutionRequestDTO.setSpaces(Collections.emptyList());
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(institutionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        institutionRequestDTO.setName(null);
        createShouldReturnStatus(institutionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        institutionRequestDTO.setId(2L);
        updateShouldReturnStatus(institutionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        institutionRequestDTO.setId(300L);
        updateShouldReturnStatus(institutionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/54", HttpStatus.CONFLICT);
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

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "99"))
            ),
            HttpStatus.CONFLICT
        );
    }

    @Test
    void findAllShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL, HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void findAllShouldReturnOk() {
        findShouldReturnStatus(baseURL, HttpStatus.OK);
    }

    @Test
    void filterShouldReturnPage0() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(new GenericFilterDTO())))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void filterShouldReturnInstitution1() {
        var filter = new GenericFilterDTO();
        filter.setQuery("unioeste");
        filter.setQueryFields(List.of("shortName"));
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
    void findOneShouldReturnInstitution() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/66", HttpStatus.NOT_FOUND);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("universidade estadual o oeste do parana"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("Universidade de São Paulo"))
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
