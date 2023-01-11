package celtab.swge.controller;


import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.TrackRequestDTO;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/track_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TrackControllerTest extends GenericTestController {

    private TrackRequestDTO trackRequestDTO;

    public TrackControllerTest() {
        baseURL = "/api/tracks";
    }

    @BeforeEach
    private void init() {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        trackRequestDTO = new TrackRequestDTO();
        trackRequestDTO.setName("Dados Criptografados");
        trackRequestDTO.setInitialDate(new Date());
        trackRequestDTO.setFinalDate(Date.from(LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));
        trackRequestDTO.setEdition(editionRequestDTO);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(trackRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        trackRequestDTO.setName(null);
        createShouldReturnStatus(trackRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        trackRequestDTO.setId(3L);
        updateShouldReturnStatus(trackRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        trackRequestDTO.setId(21L);
        updateShouldReturnStatus(trackRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnTrack() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
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
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("2", "3"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "22"))), HttpStatus.CONFLICT);
    }

    @Test
    void filterShouldReturnTrack() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Backup Corporativo");
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
                baseURL + "/unique/edition/name",
                Map.of("name", List.of("Segurança da Informação"), "editionId", List.of("1"))
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
                Map.of("name", List.of("Segurança"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }
}
