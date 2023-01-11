package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Edition;
import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/tutored_registration_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TutoredRegistrationServiceTest extends GenericTestService {

    private TutoredRegistration tutoredRegistration;

    @Autowired
    private TutoredRegistrationService tutoredRegistrationService;

    @BeforeEach
    private void init() {
        var edition = new Edition();
        edition.setId(2L);
        var user = new TutoredUser();
        user.setId(2L);
        tutoredRegistration = new TutoredRegistration();
        tutoredRegistration.setTutoredUser(user);
        tutoredRegistration.setEdition(edition);
        tutoredRegistration.setPayed(true);
        tutoredRegistration.setOriginalPrice(0d);
        tutoredRegistration.setFinalPrice(0d);
        tutoredRegistration.setIndividualRegistrations(Collections.emptyList());
    }

    @Test
    void saveNewRegistrationShouldReturnRegistration() {
        var result = tutoredRegistrationService.save(tutoredRegistration);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveUpdateRegistrationShouldReturnRegistration() {
        tutoredRegistration.setId(1L);
        tutoredRegistration.setOriginalPrice(42.0);
        tutoredRegistration.setRegistrationDateTime(Date.from(Instant.now()));
        var result = tutoredRegistrationService.save(tutoredRegistration);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewRegistrationShouldThrowException() {
        tutoredRegistration.setOriginalPrice(null);
        assertThrows(ServiceException.class,
            () -> tutoredRegistrationService.save(tutoredRegistration)
        );
    }

    @Test
    void deleteRegistrationShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> tutoredRegistrationService.delete(21L)
        );
    }

    @Test
    void deleteRegistrationShouldNotThrowException() {
        assertDoesNotThrow(
            () -> tutoredRegistrationService.delete(1L)
        );
    }

    @Test
    void deleteAllRegistrationShouldNotThrowException() {
        assertDoesNotThrow(
            () -> tutoredRegistrationService.deleteAll(List.of(1L))
        );
    }

    @Test
    void deleteAllRegistrationShouldThrowException() {
        var list = List.of(21L, 24L);
        assertThrows(ServiceException.class,
            () -> tutoredRegistrationService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableRegistrationShouldReturnPage0() {
        var result = tutoredRegistrationService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnRegistrations() {
        var result = tutoredRegistrationService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnRegistrations() {
        var result = tutoredRegistrationService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeRegistrations() {
        var result = tutoredRegistrationService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllRegistrations() {
        var filter = new GenericFilterDTO();
        var result = tutoredRegistrationService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeRegistration() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("edition.id"));
        var result = tutoredRegistrationService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnRegistration() {
        var result = tutoredRegistrationService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullRegistration() {
        var result = tutoredRegistrationService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findOneByEditionAndTutoredUserShouldReturnRegistration() {
        var result = tutoredRegistrationService.findOneByEditionAndTutoredUser(2L, 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneByEditionAndTutoredUserShouldReturnNullRegistration() {
        var result = tutoredRegistrationService.findOneByEditionAndTutoredUser(2L, 21L);
        assertNull(result);
    }
}
