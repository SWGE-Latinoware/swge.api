package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Schedule;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.registration.individual_registration.IndividualRegistrationSchedule;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/schedule_data.sql",
    "classpath:db.scripts/registration_data.sql",
    "classpath:db.scripts/individual_registration_data.sql",
    "classpath:db.scripts/individual_registration_schedule_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class IndividualRegistrationScheduleServiceTest extends GenericTestService {

    private IndividualRegistrationSchedule individualRegistrationSchedule;

    @Autowired
    private IndividualRegistrationScheduleService individualRegistrationScheduleService;

    @BeforeEach
    private void init() {
        var schedule = new Schedule();
        schedule.setId(1L);
        var individualRegistration = new IndividualRegistration();
        individualRegistration.setId(1L);
        individualRegistrationSchedule = new IndividualRegistrationSchedule();
        individualRegistrationSchedule.setSchedule(schedule);
        individualRegistrationSchedule.setIndividualRegistration(individualRegistration);
    }

    @Test
    void saveNewIndividualRegistrationScheduleShouldReturnRegistration() {
        var result = individualRegistrationScheduleService.save(individualRegistrationSchedule);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveUpdateIndividualRegistrationScheduleShouldReturnRegistration() {
        individualRegistrationSchedule.setId(1L);
        individualRegistrationSchedule.getSchedule().setId(2L);
        var result = individualRegistrationScheduleService.save(individualRegistrationSchedule);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewIndividualRegistrationScheduleShouldThrowException() {
        individualRegistrationSchedule.setSchedule(null);
        assertThrows(ServiceException.class,
            () -> individualRegistrationScheduleService.save(individualRegistrationSchedule)
        );
    }

    @Test
    void deleteIndividualRegistrationScheduleShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> individualRegistrationScheduleService.delete(21L)
        );
    }

    @Test
    void deleteIndividualRegistrationScheduleShouldNotThrowException() {
        assertDoesNotThrow(
            () -> individualRegistrationScheduleService.delete(1L)
        );
    }

    @Test
    void deleteAllIndividualRegistrationScheduleShouldNotThrowException() {
        assertDoesNotThrow(
            () -> individualRegistrationScheduleService.deleteAll(List.of(1L))
        );
    }

    @Test
    void deleteAllIndividualRegistrationScheduleShouldThrowException() {
        var list = List.of(21L, 24L);
        assertThrows(ServiceException.class,
            () -> individualRegistrationScheduleService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableIndividualRegistrationScheduleShouldReturnPage0() {
        var result = individualRegistrationScheduleService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnRegistrations() {
        var result = individualRegistrationScheduleService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnRegistrations() {
        var result = individualRegistrationScheduleService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeRegistrations() {
        var result = individualRegistrationScheduleService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllRegistrations() {
        var filter = new GenericFilterDTO();
        var result = individualRegistrationScheduleService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeRegistration() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Arduino");
        filter.setQueryFields(List.of("individualRegistration.activity.name"));
        var result = individualRegistrationScheduleService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnRegistration() {
        var result = individualRegistrationScheduleService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullRegistration() {
        var result = individualRegistrationScheduleService.findOne(21L);
        assertNull(result);
    }

    @Test
    void deleteAllByActivityAndNotInScheduleListShouldNotThrowException() {
        assertDoesNotThrow(
            () -> individualRegistrationScheduleService.deleteAllByActivityAndNotInScheduleList(1L, List.of(1L))
        );
    }

}
