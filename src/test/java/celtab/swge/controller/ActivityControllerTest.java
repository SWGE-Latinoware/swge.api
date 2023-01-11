package celtab.swge.controller;

import celtab.swge.dto.*;
import celtab.swge.model.enums.ActivityType;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.Period;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/activity_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ActivityControllerTest extends GenericTestController {

    private ActivityRequestDTO activityRequestDTO;

    public ActivityControllerTest() {
        baseURL = "/api/activities";
    }

    @BeforeEach
    private void init() {
        var attendeeCertificate = new CertificateRequestDTO();
        attendeeCertificate.setId(1L);
        var speakerCertificate = new CertificateRequestDTO();
        speakerCertificate.setId(2L);
        var trackRequestDTO = new TrackRequestDTO();
        var edition = new EditionRequestDTO();
        edition.setId(2L);
        trackRequestDTO.setId(1L);
        trackRequestDTO.setEdition(edition);
        activityRequestDTO = new ActivityRequestDTO();
        activityRequestDTO.setName("Workshop de Inteligência Artificial");
        activityRequestDTO.setLanguage("Português");
        activityRequestDTO.setVacancies(6);
        activityRequestDTO.setPrice(90.00);
        activityRequestDTO.setWorkload("12:00");
        activityRequestDTO.setType(ActivityType.WORKSHOP);
        activityRequestDTO.setPresentationType(EditionType.ONLINE);
        activityRequestDTO.setTrack(trackRequestDTO);
        activityRequestDTO.setAttendeeCertificate(attendeeCertificate);
        activityRequestDTO.setSpeakerCertificate(speakerCertificate);
        activityRequestDTO.setSpeakers(Collections.emptyList());
        activityRequestDTO.setSchedule(Collections.emptyList());
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequestSpeaker() {
        var speakerActivity1 = new SpeakerActivityRequestDTO();
        var user1 = new UserRequestDTO();
        user1.setId(1L);
        speakerActivity1.setSpeaker(user1);

        var speakerActivity2 = new SpeakerActivityRequestDTO();
        var user2 = new UserRequestDTO();
        speakerActivity2.setSpeaker(user2);
        user2.setId(3L);
        activityRequestDTO.setSpeakers(List.of(speakerActivity1, speakerActivity2));
        createShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequest() {
        activityRequestDTO.setName(null);
        createShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        activityRequestDTO.setId(1L);
        updateShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequestSpeaker() {
        var speakerActivity1 = new SpeakerActivityRequestDTO();
        var user1 = new UserRequestDTO();
        user1.setId(1L);
        speakerActivity1.setSpeaker(user1);

        var speakerActivity2 = new SpeakerActivityRequestDTO();
        var user2 = new UserRequestDTO();
        user2.setId(3L);
        speakerActivity2.setSpeaker(user2);
        activityRequestDTO.setSpeakers(List.of(speakerActivity1, speakerActivity2));
        updateShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnBadRequest() {
        activityRequestDTO.setId(21L);
        updateShouldReturnStatus(activityRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnActivity() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findAllShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/edition/1", HttpStatus.OK);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatusAndBody(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
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
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "22"))), HttpStatus.CONFLICT);
    }

    @Test
    void filterShouldReturnActivity() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Workshop");
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
                Map.of("name", List.of("Mini Curso de Arduino"), "editionId", List.of("1"))
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
                Map.of("name", List.of("Arduino"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateScheduleShouldReturnBadRequestTrackNotValid() {
        activityRequestDTO.getTrack().setId(8L);

        createShouldReturnStatus(
            activityRequestDTO,
            baseURL,
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void validateScheduleShouldReturnBadRequestWrongInterval() {
        var track = activityRequestDTO.getTrack();

        track.setFinalDate(Date.from(Instant.now().plus(Period.ofDays(10))));
        track.setInitialDate(Date.from(Instant.now()));

        var schedule = new ScheduleRequestDTO();
        schedule.setStartDateTime(Date.from(Instant.now()));
        schedule.setEndDateTime(Date.from(Instant.now().plus(Period.ofDays(5))));

        activityRequestDTO.setSchedule(List.of(schedule));

        createShouldReturnStatus(
            activityRequestDTO,
            baseURL,
            HttpStatus.BAD_REQUEST
        );
    }
}
