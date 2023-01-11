package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Activity;
import celtab.swge.model.Edition;
import celtab.swge.model.enums.PaymentType;
import celtab.swge.model.registration.Registration;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.user.User;
import celtab.swge.util.GenericTestService;
import celtab.swge.util.UUIDUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Date;
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
    "classpath:db.scripts/registration_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RegistrationServiceTest extends GenericTestService implements UUIDUtils {

    private Registration registration;

    @Autowired
    private RegistrationService registrationService;

    @BeforeEach
    private void init() {
        var edition = new Edition();
        edition.setId(2L);
        var user = new User();
        user.setId(1L);
        var activity = new Activity();
        activity.setId(2L);
        var individualRegistration = new IndividualRegistration();
        individualRegistration.setActivity(activity);
        registration = new Registration();
        registration.setUserRegistrationId(getRandomUUIDString());
        registration.setIndividualRegistrations(List.of(individualRegistration));
        registration.setUser(user);
        registration.setEdition(edition);
        registration.setPayed(false);
        registration.setOriginalPrice(100.0);
        registration.setFinalPrice(30.0);
        registration.setPaymentType(PaymentType.NONE);
    }

    @Test
    void saveNewRegistrationShouldReturnRegistration() {
        var result = registrationService.save(registration);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveUpdateRegistrationShouldReturnRegistration() {
        registration.setId(1L);
        registration.setOriginalPrice(0.0);
        registration.setRegistrationDateTime(Date.from(Instant.now()));
        var result = registrationService.save(registration);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewRegistrationShouldThrowException() {
        registration.setOriginalPrice(null);
        assertThrows(ServiceException.class,
            () -> registrationService.save(registration)
        );
    }

    @Test
    void deleteRegistrationShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> registrationService.delete(21L)
        );
    }

    @Test
    void deleteRegistrationShouldNotThrowException() {
        assertDoesNotThrow(
            () -> registrationService.delete(1L)
        );
    }

    @Test
    void deleteAllRegistrationShouldNotThrowException() {
        assertDoesNotThrow(
            () -> registrationService.deleteAll(List.of(1L))
        );
    }

    @Test
    void deleteAllRegistrationShouldThrowException() {
        var list = List.of(21L, 24L);
        assertThrows(ServiceException.class,
            () -> registrationService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableRegistrationShouldReturnPage0() {
        var result = registrationService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnRegistrations() {
        var result = registrationService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnRegistrations() {
        var result = registrationService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeRegistrations() {
        var result = registrationService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllRegistrations() {
        var filter = new GenericFilterDTO();
        var result = registrationService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeRegistration() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("edition.id"));
        var result = registrationService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnRegistration() {
        var result = registrationService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullRegistration() {
        var result = registrationService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findOneByEditionAndUserShouldReturnRegistration() {
        var result = registrationService.findOneByEditionAndUser(2L, 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneByEditionAndUserShouldReturnNullRegistration() {
        var result = registrationService.findOneByEditionAndUser(2L, 21L);
        assertNull(result);
    }
}
