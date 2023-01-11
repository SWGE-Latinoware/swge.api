package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
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
    "classpath:db.scripts/tutored_user_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TutoredUserServiceTest extends GenericTestService {

    private TutoredUser tutoredUser;

    @Autowired
    private TutoredUserService tutoredUserService;

    @BeforeEach
    void init() {
        tutoredUser = new TutoredUser();
        tutoredUser.setName("Rafael");
        tutoredUser.setTagName("Rafa");
        tutoredUser.setCountry("BR");
        tutoredUser.setIdNumber("124155999");
    }

    @Test
    void saveNewTutoredUserShouldReturnTutoredUser() {
        var result = tutoredUserService.save(tutoredUser);
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void saveUpdateTutoredUserShouldReturnTutoredUser() {
        tutoredUser.setId(1L);
        var result = tutoredUserService.save(tutoredUser);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewTutoredUserShouldThrowException() {
        tutoredUser.setTagName(null);
        assertThrows(ServiceException.class,
            () -> {
                tutoredUserService.save(tutoredUser);
            }
        );
    }

    @Test
    void deleteTutoredUserShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                tutoredUserService.delete(42L);
            }
        );
    }

    @Test
    void deleteTutoredUserShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                tutoredUserService.delete(2L);
            }
        );
    }

    @Test
    void deleteAllTutoredUsersShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                tutoredUserService.deleteAll(List.of(1L, 2L));
            }
        );
    }

    @Test
    void deleteAllTutoredUsersShouldThrowException() {
        var list = List.of(1L, 42L);
        assertThrows(ServiceException.class,
            () -> {
                tutoredUserService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = tutoredUserService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllTutoredUsers() {
        var result = tutoredUserService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnTutoredUsers() {
        var result = tutoredUserService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeTutoredUsers() {
        var result = tutoredUserService.findAllById(List.of(1L, 42L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllTutoredUsers() {
        var filter = new GenericFilterDTO();
        var result = tutoredUserService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeTutoredUsers() {
        var filter = new GenericFilterDTO();
        filter.setQuery("marquinhos");
        filter.setQueryFields(List.of("name"));
        var result = tutoredUserService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnTutoredUser() {
        var result = tutoredUserService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = tutoredUserService.findOne(42L);
        assertNull(result);
    }

    @Test
    void findByIdNumberShouldReturnUser() {
        var result = tutoredUserService.findByIdNumber("12345678911");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByIdNumberShouldReturnNull() {
        var result = tutoredUserService.findByIdNumber("12345678955");
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnTutoredUser() {
        var result = tutoredUserService.findByName("clebinho");
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = tutoredUserService.findByName("Mateus");
        assertNull(result);
    }

    @Test
    void findByTagNameShouldReturnTutoredUser() {
        var result = tutoredUserService.findByTagName("clebao");
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByTagNameShouldReturnNull() {
        var result = tutoredUserService.findByName("clebao2");
        assertNull(result);
    }

}
