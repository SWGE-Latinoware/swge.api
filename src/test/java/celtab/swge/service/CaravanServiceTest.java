package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Caravan;
import celtab.swge.model.Edition;
import celtab.swge.model.Institution;
import celtab.swge.model.enums.CaravanType;
import celtab.swge.model.user.TutoredUser;
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
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/caravan_data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CaravanServiceTest extends GenericTestService {

    private Caravan caravan;

    @Autowired
    private CaravanService caravanService;

    @BeforeEach
    private void init() {
        caravan = new Caravan();
        var user = new User();
        user.setId(2L);
        var edition = new Edition();
        edition.setId(1L);
        var institution = new Institution();
        institution.setId(1L);
        caravan.setName("Caravana 4");
        caravan.setCountry("BR");
        caravan.setPayed(false);
        caravan.setPrice(250.00);
        caravan.setType(CaravanType.TUTORED);
        caravan.setVacancies(4);
        caravan.setCoordinator(user);
        caravan.setEdition(edition);
        caravan.setInstitution(institution);
    }

    @Test
    void saveNewCaravanShouldReturnCaravan() {
        var result = caravanService.save(caravan);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void saveUpdateCaravanShouldReturnCaravan() {
        caravan.setId(2L);
        var result = caravanService.save(caravan);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveNewCaravanShouldThrowException() {
        caravan.setName(null);
        assertThrows(ServiceException.class, () -> caravanService.save(caravan));
    }

    @Test
    void deleteCaravanShouldThrowException() {
        assertThrows(ServiceException.class, () -> caravanService.delete(21L));
    }

    @Test
    void deleteCaravanShouldNotThrowException() {
        assertDoesNotThrow(() -> caravanService.delete(2L));
    }

    @Test
    void deleteAllCaravanShouldNotThrowException() {
        assertDoesNotThrow(() -> caravanService.deleteAll(List.of(2L, 3L)));
    }

    @Test
    void deleteAllCaravanShouldThrowException() {
        var list = List.of(3L, 21L, 33L);
        assertThrows(ServiceException.class, () -> caravanService.deleteAll(list));
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = caravanService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllCaravan() {
        var result = caravanService.findAll();
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void findAllByIdShouldReturnCaravan() {
        var result = caravanService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeCaravan() {
        var result = caravanService.findAllById(List.of(2L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllCaravan() {
        var filter = new GenericFilterDTO();
        var result = caravanService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeCaravan() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Caravana 1");
        filter.setQueryFields(List.of("name"));
        var result = caravanService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());

    }

    @Test
    void findOneShouldReturnCaravan() {
        var result = caravanService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = caravanService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findAllByEditionShouldReturnPage0() {
        var result = caravanService.findAllByEdition(2L, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllByEditionShouldReturnOk() {
        var result = caravanService.findAllByEdition(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByEditionAndUserEnrollmentShould() {
        var result = caravanService.findAllByEditionAndUserEnrollment(1L, 2L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void caravanEnrollmentShouldReturnOk() {
        var user = new User();
        user.setId(1L);
        assertDoesNotThrow(() -> caravanService.caravanEnrollment(caravanService.findOne(1L), user, false));
    }

    @Test
    void caravanEnrollmentShouldReturnFalid() {
        var user = new User();
        user.setId(21L);
        var caravan = caravanService.findOne(2L);
        assertThrows(ServiceException.class, () -> caravanService.caravanEnrollment(caravan, user, false));
    }

    @Test
    void caravanEnrollmentShouldThrowExceptionNoRemainingVacancies() {
        var user = new User();
        user.setId(1L);
        var caravan = caravanService.findOne(4L);
        assertThrows(ServiceException.class, () -> caravanService.caravanEnrollment(caravan, user, false));
    }

    @Test
    void caravanEnrollmentListShouldReturnOk() {
        var user = new User();
        user.setId(1L);
        var user2 = new User();
        user2.setId(2L);
        assertDoesNotThrow(() -> caravanService.caravanEnrollment(caravanService.findOne(1L), List.of(user, user2)));
    }

    @Test
    void caravanEnrollmentListShouldReturnFalid() {
        var user = new User();
        user.setId(21L);
        var user2 = new User();
        user2.setId(23L);
        var list = List.of(user, user2);
        var caravan = caravanService.findOne(2L);
        assertThrows(ServiceException.class, () -> caravanService.caravanEnrollment(caravan, list));
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnOk() {
        var user = new TutoredUser();
        user.setId(1L);
        var user2 = new TutoredUser();
        user2.setId(2L);
        assertDoesNotThrow(() -> caravanService.caravanEnrollmentTutored(caravanService.findOne(3L), List.of(user, user2)));
    }

    @Test
    void caravanEnrollmentListTutoredShouldReturnFalid() {
        var user = new TutoredUser();
        user.setId(21L);
        var user2 = new TutoredUser();
        user2.setId(23L);
        var list = List.of(user, user2);
        var caravan = caravanService.findOne(2L);
        assertThrows(ServiceException.class, () -> caravanService.caravanEnrollmentTutored(caravan, list));
    }

    @Test
    void caravanEnrollmentTutoredShouldReturnOk() {
        var tutoredUser = new TutoredUser();
        tutoredUser.setId(1L);
        assertDoesNotThrow(() -> caravanService.caravanEnrollment(caravanService.findOne(1L), tutoredUser));
    }

    @Test
    void caravanEnrollentTutoredShouldReturnFalid() {
        var tutoredUser = new TutoredUser();
        tutoredUser.setId(21L);
        var caravan = caravanService.findOne(2L);

        assertThrows(ServiceException.class, () -> caravanService.caravanEnrollment(caravan, tutoredUser));
    }

    @Test
    void findByNameShouldReturnTrack() {
        var result = caravanService.findByNameAndEdition("Caravana 1", 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = caravanService.findByNameAndEdition("Caravana 6000", 1L);
        assertNull(result);
    }

    @Test
    void findAllByEditionAndCoordinatorShouldReturnList() {
        var result = caravanService.findAllByEditionAndCoordinator(1L, 1L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
