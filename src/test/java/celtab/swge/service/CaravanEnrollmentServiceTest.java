package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Caravan;
import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.model.user.User;
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
},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanEnrollmentServiceTest extends GenericTestService {

    private CaravanEnrollment caravanEnrollment;

    @Autowired
    private CaravanEnrollmentService caravanEnrollmentService;

    @BeforeEach
    private void init() {
        caravanEnrollment = new CaravanEnrollment();
        var caravan = new Caravan();
        var user = new User();
        caravanEnrollment.setAccepted(false);
        caravanEnrollment.setPayed(true);
        caravanEnrollment.setConfirmed(false);
        caravan.setId(3L);
        caravanEnrollment.setCaravan(caravan);
        user.setId(2L);
        caravanEnrollment.setUser(user);
    }

    @Test
    void saveNewCaravanEnrollmentShouldReturnCaravanEnrollment() {
        var result = caravanEnrollmentService.save(caravanEnrollment);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveUpdateCaravanEnrollmentShouldReturnCaravanEnrollment() {
        caravanEnrollment.setId(1L);
        var result = caravanEnrollmentService.save(caravanEnrollment);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewCaravanEnrollmentShouldThrowException() {
        caravanEnrollment.setPayed(null);
        assertThrows(ServiceException.class,
            () -> caravanEnrollmentService.save(caravanEnrollment)
        );
    }

    @Test
    void deleteCaravanEnrollmentShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> caravanEnrollmentService.delete(42L)
        );
    }

    @Test
    void deleteCaravanEnrollmentShouldNotThrowException() {
        assertDoesNotThrow(
            () -> caravanEnrollmentService.delete(2L)
        );
    }

    @Test
    void deleteAllCaravanEnrollmentShouldNotThrowException() {
        assertDoesNotThrow(
            () -> caravanEnrollmentService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllCaravanEnrollmentShouldThrowException() {
        var list = List.of(1L, 42L);
        assertThrows(ServiceException.class,
            () -> caravanEnrollmentService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = caravanEnrollmentService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllCaravanEnrollment() {
        var result = caravanEnrollmentService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnCaravanEnrollment() {
        var result = caravanEnrollmentService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeCaravanEnrollment() {
        var result = caravanEnrollmentService.findAllById(List.of(1L, 42L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllCaravanEnrollment() {
        var filter = new GenericFilterDTO();
        var result = caravanEnrollmentService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeCaravanEnrollment() {
        var filter = new GenericFilterDTO();
        filter.setQuery("true");
        filter.setQueryFields(List.of("confirmed"));
        var result = caravanEnrollmentService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnCaravanEnrollment() {
        var result = caravanEnrollmentService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = caravanEnrollmentService.findOne(42L);
        assertNull(result);
    }

    @Test
    void findAllByCaravanShouldReturnPage0() {
        var result = caravanEnrollmentService.findAllByCaravan(1L, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllByEditionIdAndUserIdShouldReturnList() {
        var result = caravanEnrollmentService.findAllByEditionIdAndUserId(1L, 2L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

}
