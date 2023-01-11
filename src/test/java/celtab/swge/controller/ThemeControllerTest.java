package celtab.swge.controller;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.ThemeRequestDTO;
import celtab.swge.model.enums.ThemeType;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/theme_data.sql",
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ThemeControllerTest extends GenericTestController {

    private ThemeRequestDTO themeRequestDTO;

    public ThemeControllerTest() {
        baseURL = "/api/themes";
    }

    @BeforeEach
    private void init() {
        themeRequestDTO = new ThemeRequestDTO();
        themeRequestDTO.setName("paleta 2020");
        themeRequestDTO.setType(ThemeType.DARK);
        themeRequestDTO.setColorPalette(Map.of("primary", "#4caf50", "secondary", "#292d31", "error", "#f44336"));
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(themeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        themeRequestDTO.setName(null);
        createShouldReturnStatus(themeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        themeRequestDTO.setId(2L);
        updateShouldReturnStatus(themeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        themeRequestDTO.setId(21L);
        updateShouldReturnStatus(themeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/2", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1", "2"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1", "21"))), HttpStatus.CONFLICT);
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
    void filterShouldReturnTheme() {
        var filter = new GenericFilterDTO();
        filter.setQuery("paleta 4");
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
    void findOneShouldReturnTheme() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("paleta 1"))
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
                Map.of("name", List.of("paleta 100"))
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
