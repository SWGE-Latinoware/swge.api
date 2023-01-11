package celtab.swge.controller;

import celtab.swge.dto.EditionHomeRequestDTO;
import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.InstitutionRequestDTO;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/edition_home_data.sql",
    "classpath:db.scripts/registration_data.sql",
    "classpath:db.scripts/user_permission_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/speaker_activity_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EditionControllerTest extends GenericTestController {

    private EditionRequestDTO editionRequestDTO;

    private EditionHomeRequestDTO editionHomeRequestDTO;

    public EditionControllerTest() {
        baseURL = "/api/editions";
    }

    @BeforeEach
    private void init() {
        editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setEnabled(true);
        editionRequestDTO.setName("21 edition");
        editionRequestDTO.setShortName("21");
        editionRequestDTO.setYear(2021);
        editionRequestDTO.setInitialDate(new Date());
        editionRequestDTO.setFinalDate(Date.from(LocalDate.now().plusDays(3).atStartOfDay().toInstant(ZoneOffset.UTC)));
        editionRequestDTO.setType(EditionType.ONLINE);
        var institution = new InstitutionRequestDTO();
        institution.setId(1L);
        editionRequestDTO.setInstitution(institution);

        var homeContent = new HashMap<String, Object>();
        homeContent.put("data", "Test 1");

        var secondEdition = new EditionRequestDTO();
        secondEdition.setId(2L);
        editionHomeRequestDTO = new EditionHomeRequestDTO();
        editionHomeRequestDTO.setEdition(secondEdition);
        editionHomeRequestDTO.setLanguage("pt-BR");
        editionHomeRequestDTO.setHomeContent(homeContent);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(editionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        editionRequestDTO.setName(null);
        createShouldReturnStatus(editionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        editionRequestDTO.setId(2L);
        updateShouldReturnStatus(editionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        editionRequestDTO.setId(99L);
        updateShouldReturnStatus(editionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

   /* @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }*/

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/55", HttpStatus.CONFLICT);
    }

   /* @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "3"))
            ),
            HttpStatus.OK
        );
    }*/

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "96"))
            ),
            HttpStatus.CONFLICT
        );
    }

    @Test
    void findAllShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL, HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(3);
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
            .jsonPath("$.totalElements").isEqualTo(3);
    }

    @Test
    void filterShouldReturnEdition18() {
        var filter = new GenericFilterDTO();
        filter.setQuery("18 edicao");
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
    void findOneShouldReturnEdition() {
        findShouldReturnStatusAndBody(baseURL + "/2", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(2L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/68", HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllCoordinatorsShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/coordinators", HttpStatus.OK);
    }

    @Test
    void isCoordinatorShouldReturnOk() {
        findShouldReturnStatusAndBody(baseURL + "/2/coordinator/1", HttpStatus.OK)
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void isCoordinatorShouldReturnFalseCoordinator() {
        findShouldReturnStatusAndBody(baseURL + "/2/coordinator/42", HttpStatus.OK)
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void isSpeakerShouldReturnOk() {
        findShouldReturnStatusAndBody(baseURL + "/2/speaker/2", HttpStatus.OK)
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void isSpeakerShouldReturnFalseSpeaker() {
        findShouldReturnStatusAndBody(baseURL + "/2/speaker/10", HttpStatus.OK)
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void findAllCaravansByCoordinatorShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/2/coordinator/1/caravans", HttpStatus.OK);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("18 edicao"))
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
                Map.of("name", List.of("0 edicao"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueShortNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/short-name",
                Map.of("shortName", List.of("18"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueShortNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/short-name",
                Map.of("shortName", List.of("0"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueYearShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/year",
                Map.of("year", List.of("2018"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueYearShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/year",
                Map.of("year", List.of("1912"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void findAllActivitiesBySpeakerShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/2/speaker/2/activities", HttpStatus.OK);
    }

    @Test
    void findAllActivitiesBySpeakerShouldReturnNull() {
        findShouldReturnStatus(baseURL + "/4/speaker/10/activities", HttpStatus.OK);
    }

    @Test
    void findAllSpeakersShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/speakers", HttpStatus.OK);
    }

    @Test
    void findAllCertificatesShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/certificates", HttpStatus.OK);
    }

    @Test
    void findAllTracksShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/tracks", HttpStatus.OK);
    }

    @Test
    void findAllByListShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/list", HttpStatus.OK);
    }

    @Test
    void createEditionHomeShouldReturnOk() {
        createShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.OK);
    }

    @Test
    void createEditionHomeShouldReturnBadRequestAlreadyHasEditionHomeContent() {
        var firstEdition = new EditionRequestDTO();
        firstEdition.setId(1L);
        editionHomeRequestDTO.setEdition(firstEdition);
        createShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.BAD_REQUEST);
    }

    @Test
    void createEditionHomeShouldReturnBadRequestNoLanguage() {
        editionHomeRequestDTO.setLanguage(null);
        createShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateEditionHomeShouldReturnOk() {
        var firstEdition = new EditionRequestDTO();
        firstEdition.setId(1L);
        editionHomeRequestDTO.setEdition(firstEdition);
        editionHomeRequestDTO.setId(1L);
        updateShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.OK);
    }

    @Test
    void updateEditionHomeShouldReturnNotFound() {
        editionHomeRequestDTO.setId(2L);
        updateShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.NOT_FOUND);
    }

    @Test
    void updateEditionHomeShouldReturnBadRequestNoLanguage() {
        editionHomeRequestDTO.setId(1L);
        editionHomeRequestDTO.setLanguage(null);
        updateShouldReturnStatus(editionHomeRequestDTO, baseURL + "/languageContent", HttpStatus.BAD_REQUEST);
    }

    @Test
    void findEditionCaravansShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/info/caravans", HttpStatus.OK);
    }

    @Test
    void findEditionCertificatesShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/info/certificates", HttpStatus.OK);
    }

    @Test
    void findEditionTracksShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/info/tracks", HttpStatus.OK);
    }

    @Test
    void findEditionRegistrationsShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/info/registrations", HttpStatus.OK);
    }

    @Test
    void findEditionActivityShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/info/activities", HttpStatus.OK);
    }

    @Test
    void findEditionGridCoordinatorsShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/grid-coordinators", HttpStatus.OK);
    }

    @Test
    void findEditionHomeByLanguageShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/languageContent/pt-BR", HttpStatus.OK);
    }

    @Test
    void findEditionHomeByEditionAndLanguageShouldReturnNotFoundNoEdition() {
        findShouldReturnStatus(baseURL + "/2/languageContent/pt-BR", HttpStatus.NOT_FOUND);
    }

    @Test
    void findEditionHomeByEditionAndLanguageShouldReturnNotFoundNoLanguage() {
        findShouldReturnStatus(baseURL + "/1/languageContent/en-US", HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllEditionHomeByEditionShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/1/languageContent", HttpStatus.OK);
    }

    @Test
    void findAllEditionHomeByEditionShouldReturnNotFoundNoEditionContent() {
        findShouldReturnStatus(baseURL + "/2/languageContent", HttpStatus.NOT_FOUND);
    }
}
