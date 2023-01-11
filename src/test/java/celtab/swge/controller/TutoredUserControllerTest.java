package celtab.swge.controller;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.TutoredUserRequestDTO;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TutoredUserControllerTest extends GenericTestController {

    private TutoredUserRequestDTO tutoredUserRequestDTO;

    public TutoredUserControllerTest() {
        baseURL = "/api/tutored-users";
    }

    @BeforeEach
    private void init() {
        tutoredUserRequestDTO = new TutoredUserRequestDTO();
        tutoredUserRequestDTO.setName("Joao");
        tutoredUserRequestDTO.setTagName("Jao");
        tutoredUserRequestDTO.setBirthDate(new Date());
        tutoredUserRequestDTO.setCountry("BR");
        tutoredUserRequestDTO.setIdNumber("123456789123");
    }

    @Test
    void updateShouldReturnOk() {
        tutoredUserRequestDTO.setId(1L);
        updateShouldReturnStatus(tutoredUserRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        tutoredUserRequestDTO.setId(21L);
        updateShouldReturnStatus(tutoredUserRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnTutoredUser() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
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
    void filterShouldReturnUser1() {
        var filter = new GenericFilterDTO();
        filter.setQuery("marquinhos");
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
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("marquinhos"))
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
                Map.of("name", List.of("marcos"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueTagNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/tag-name",
                Map.of("tagName", List.of("clebao"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }


    @Test
    void validateUniqueTagNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/tag-name",
                Map.of("tagName", List.of("cleber"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueIdNumberShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/id-number",
                Map.of("idNumber", List.of("12345678911"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }


    @Test
    void validateUniqueIdNumberShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/id-number",
                Map.of("idNumber", List.of("12345678950"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
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
}
