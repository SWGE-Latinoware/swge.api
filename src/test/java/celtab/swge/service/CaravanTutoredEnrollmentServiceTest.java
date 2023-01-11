package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Caravan;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.user.TutoredUser;
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
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/caravan_data.sql",
    "classpath:db.scripts/caravan_enrollment_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/caravan_tutored_enrollment_data.sql"
},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanTutoredEnrollmentServiceTest extends GenericTestService {

    private CaravanTutoredEnrollment caravanTutoredEnrollment;

    @Autowired
    private CaravanTutoredEnrollmentService caravanTutoredEnrollmentService;

    @BeforeEach
    private void init() {
        var caravan = new Caravan();
        var tutoredUser = new TutoredUser();
        caravanTutoredEnrollment = new CaravanTutoredEnrollment();
        caravanTutoredEnrollment.setAccepted(true);
        caravanTutoredEnrollment.setPayed(false);
        caravan.setId(1L);
        caravanTutoredEnrollment.setCaravan(caravan);
        tutoredUser.setId(1L);
        caravanTutoredEnrollment.setTutoredUser(tutoredUser);
    }

    @Test
    void saveNewCaravanTutoredEnrollmentShouldReturnCaravanEnrollment() {
        var result = caravanTutoredEnrollmentService.save(caravanTutoredEnrollment);
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void saveNewCaravanTutoredEnrollmentShouldThrowException() {
        caravanTutoredEnrollment.setPayed(null);
        assertThrows(ServiceException.class,
            () -> caravanTutoredEnrollmentService.save(caravanTutoredEnrollment)
        );
    }

    @Test
    void saveUpdateCaravanTutoredEnrollmentShouldReturnCaravanTutoredEnrollment() {
        caravanTutoredEnrollment.setId(1L);
        var result = caravanTutoredEnrollmentService.save(caravanTutoredEnrollment);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findAllTutoredByCaravanShouldReturnPage0() {
        var result = caravanTutoredEnrollmentService.findAllByCaravan(1L, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void deleteCaravanTutoredEnrollmentShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> caravanTutoredEnrollmentService.delete(42L)
        );
    }

    @Test
    void deleteCaravanTutoredEnrollmentShouldNotThrowException() {
        assertDoesNotThrow(
            () -> caravanTutoredEnrollmentService.delete(1L)
        );
    }

    @Test
    void deleteAllCaravanTutoredEnrollmentShouldNotThrowException() {
        assertDoesNotThrow(
            () -> caravanTutoredEnrollmentService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllCaravanTutoredEnrollmentShouldThrowException() {
        var list = List.of(1L, 42L);
        assertThrows(ServiceException.class,
            () -> caravanTutoredEnrollmentService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = caravanTutoredEnrollmentService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllCaravanTutoredEnrollment() {
        var result = caravanTutoredEnrollmentService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnCaravanTutoredEnrollment() {
        var result = caravanTutoredEnrollmentService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeCaravanTutoredEnrollment() {
        var result = caravanTutoredEnrollmentService.findAllById(List.of(1L, 42L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllCaravanTutoredEnrollment() {
        var filter = new GenericFilterDTO();
        var result = caravanTutoredEnrollmentService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeCaravanTutoredEnrollment() {
        var filter = new GenericFilterDTO();
        filter.setQuery("true");
        filter.setQueryFields(List.of("payed"));
        var result = caravanTutoredEnrollmentService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnCaravanTutoredEnrollment() {
        var result = caravanTutoredEnrollmentService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = caravanTutoredEnrollmentService.findOne(42L);
        assertNull(result);
    }

}
