package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.RegistrationType;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RegistrationTypeServiceTest extends GenericTestService {

    private RegistrationType registrationType;

    @Autowired
    private RegistrationTypeService registrationTypeService;

    @BeforeEach
    private void init() {
        registrationType = new RegistrationType();
        registrationType.setInitialDateTime(new Date());
        registrationType.setFinalDateTime(Date.from(LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));
        registrationType.setPrice(100.0);
        registrationType.setPromotions(Collections.emptyList());
    }

    @Test
    void saveNewShouldReturnObject() {
        var result = registrationTypeService.save(registrationType);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveUpdateShouldReturnObject() {
        registrationType.setId(1L);
        var result = registrationTypeService.save(registrationType);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewShouldThrowException() {
        registrationType.setPrice(null);
        assertThrows(ServiceException.class,
            () -> {
                registrationTypeService.save(registrationType);
            }
        );
    }

    @Test
    void deleteShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                registrationTypeService.delete(21L);
            }
        );
    }

    @Test
    void deleteShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                registrationTypeService.delete(1L);
            }
        );
    }

    @Test
    void deleteAllShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                registrationTypeService.deleteAll(List.of(1L));
            }
        );
    }

    @Test
    void deleteAllShouldThrowException() {
        var list = List.of(1L, 24L);
        assertThrows(ServiceException.class,
            () -> {
                registrationTypeService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = registrationTypeService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnObjects() {
        var result = registrationTypeService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnObjects() {
        var result = registrationTypeService.findAllById(List.of(1L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeObjects() {
        var result = registrationTypeService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllObjects() {
        var filter = new GenericFilterDTO();
        var result = registrationTypeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeObjects() {
        var filter = new GenericFilterDTO();
        filter.setQuery("150");
        filter.setQueryFields(List.of("price"));
        var result = registrationTypeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnObject() {
        var result = registrationTypeService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = registrationTypeService.findOne(21L);
        assertNull(result);
    }
}
