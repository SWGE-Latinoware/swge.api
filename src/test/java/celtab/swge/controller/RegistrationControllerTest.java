package celtab.swge.controller;


import celtab.swge.dto.*;
import celtab.swge.model.Caravan;
import celtab.swge.service.CaravanEnrollmentService;
import celtab.swge.service.EditionService;
import celtab.swge.service.RegistrationTypeService;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/promotion_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/registration_data.sql",
    "classpath:db.scripts/individual_registration_data.sql",
    "classpath:db.scripts/schedule_data.sql",
    "classpath:db.scripts/individual_registration_schedule_data.sql",
    "classpath:db.scripts/caravan_data.sql",
    "classpath:db.scripts/caravan_enrollment_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RegistrationControllerTest extends GenericTestController {

    private RegistrationRequestDTO registrationRequestDTO;

    @Autowired
    private RegistrationTypeService registrationTypeService;

    @Autowired
    private EditionService editionService;

    @Autowired
    private CaravanEnrollmentService caravanEnrollmentService;

    public RegistrationControllerTest() {
        baseURL = "/api/registrations";
    }

    @BeforeEach
    private void init() {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(2L);
        var activity = new ActivityRequestDTO();
        activity.setId(2L);
        var individualRegistration = new IndividualRegistrationRequestDTO();
        individualRegistration.setActivity(activity);
        registrationRequestDTO = new RegistrationRequestDTO();
        registrationRequestDTO.setIndividualRegistrations(List.of(individualRegistration));
        registrationRequestDTO.setUser(userRequestDTO);
        registrationRequestDTO.setEdition(editionRequestDTO);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnOkWithUserInCaravan() {
        var caravanEnroll = caravanEnrollmentService.findOne(1L);

        var caravan = new Caravan();
        caravan.setId(3L);
        caravanEnroll.setCaravan(caravan);
        caravanEnrollmentService.save(caravanEnroll);

        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequestByRegistrationTypeWrongPeriod() {
        var regType = registrationTypeService.findOne(1L);
        regType.setFinalDateTime(Date.from(Instant.now().minus(Period.ofDays(10))));
        registrationTypeService.save(regType);
        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByRegistrationTypeNull() {
        var edit = editionService.findOne(2L);
        edit.setRegistrationType(null);
        editionService.save(edit);

        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnConflictUserRegistered() {
        createShouldReturnOk();

        RegistrationRequestDTO secondRegistrationRequestDTO = new RegistrationRequestDTO();
        secondRegistrationRequestDTO.setIndividualRegistrations(registrationRequestDTO.getIndividualRegistrations());
        secondRegistrationRequestDTO.setUser(registrationRequestDTO.getUser());
        secondRegistrationRequestDTO.setEdition(registrationRequestDTO.getEdition());

        createShouldReturnStatus(secondRegistrationRequestDTO, baseURL, HttpStatus.CONFLICT);
    }

    @Test
    void createShouldReturnBadRequest() {
        registrationRequestDTO.setIndividualRegistrations(null);
        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnConflictFullActivity() {
        registrationRequestDTO.getIndividualRegistrations().get(0).getActivity().setId(4L);
        createShouldReturnStatus(registrationRequestDTO, baseURL, HttpStatus.CONFLICT);
    }

    @Test
    void findOneShouldReturnRegistration() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneByEditionAndUserShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/edition/1/registration/2", HttpStatus.NOT_FOUND);
    }

    @Test
    void findOneByEditionAndUserShouldReturnRegistration() {
        findShouldReturnStatusAndBody(baseURL + "/edition/2/registration/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void cancelRegistrationShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/cancel/1", HttpStatus.OK);
    }

    @Test
    void cancelRegistrationShouldReturnForbidden() {
        deleteShouldReturnStatus(baseURL + "/cancel/42", HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "22"))), HttpStatus.CONFLICT);
    }

    @Test
    void filterShouldReturnTrack() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("edition.id"));
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
    void updateUserScheduleListShouldReturnOk() {
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);
        registrationRequestDTO.setUser(userRequestDTO);
        updateShouldReturnStatus(registrationRequestDTO, new URIObject(
            baseURL + "/schedule-list/1",
            Map.of("schedulesId", List.of("1"))
        ), HttpStatus.OK);
    }

    @Test
    void updateUserScheduleListShouldReturnNotFoundScheduleList() {
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);
        registrationRequestDTO.setUser(userRequestDTO);
        updateShouldReturnStatus(registrationRequestDTO, new URIObject(
            baseURL + "/schedule-list/1",
            Map.of("schedulesId", List.of("3"))
        ), HttpStatus.NOT_FOUND);
    }
}
