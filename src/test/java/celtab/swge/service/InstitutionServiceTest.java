package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Institution;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InstitutionServiceTest extends GenericTestService {

    private Institution institution;

    @Autowired
    private InstitutionService institutionService;

    @BeforeEach
    private void init() {
        institution = new Institution();
        institution.setName("Universidade das Cataratas");
        institution.setShortName("UDC");
        institution.setPhone("45998707070");
        institution.setWebsite("www.udc.com.br");
        institution.setCity("Foz do Iguacu");
        institution.setState("PR");
        institution.setCountry("BR");
        institution.setSpaces(Collections.emptyList());
    }

    @Test
    void saveNewInstitutionShouldReturnInstitution() {
        var result = institutionService.save(institution);
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void saveUpdateInstitutionShouldReturnInstitution() {
        institution.setId(1L);
        var result = institutionService.save(institution);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewInstitutionShouldThrowException() {
        institution.setName(null);
        assertThrows(ServiceException.class,
            () -> institutionService.save(institution)
        );
    }

    @Test
    void deleteShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> institutionService.delete(55L)
        );
    }

    @Test
    void deleteInstitutionShouldNotThrowException() {
        assertDoesNotThrow(
            () -> institutionService.delete(1L)
        );
    }

    @Test
    void deleteAllInstitutionsShouldNotThrowException() {
        assertDoesNotThrow(
            () -> institutionService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllInstitutionsShouldThrowException() {
        var list = List.of(1L, 50L);
        assertThrows(ServiceException.class,
            () -> institutionService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = institutionService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllInstitutions() {
        var result = institutionService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnInstitutions() {
        var result = institutionService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeInstitutions() {
        var result = institutionService.findAllById(List.of(1L, 34L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllUsers() {
        var filter = new GenericFilterDTO();
        var result = institutionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeInstitution() {
        var filter = new GenericFilterDTO();
        filter.setQuery("unioeste");
        filter.setQueryFields(List.of("shortName"));
        var result = institutionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnInstitution() {
        var result = institutionService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = institutionService.findOne(69L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnInstitution() {
        var result = institutionService.findByName("universidade estadual o oeste do parana");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = institutionService.findByName("Universidade de São Paulo");
        assertNull(result);
    }

}
