package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.user.User;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"classpath:db.scripts/users_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTest extends GenericTestService {

    private User user;

    @Autowired
    private UserService userService;

    @BeforeEach
    private void init() {
        user = new User();
        user.setName("Mateus S.");
        user.setEmail("mateus@gmail.teste.com");
        user.setCountry("US");
        user.setZipCode("89652378");
        user.setState("ES");
        user.setCity("Los Angeles");
        user.setAddressLine1("Saint Patricks Cathedral");
        user.setPassword("$2a$10$62vjXA8eARNq4du7TWuhjORpo08S8Fqc1gxftUpiT6o2Q5puWIL7G");
        user.setTagName("mateus");
        user.setAdmin(false);
        user.setEmailCommunication(true);
        user.setEnabled(true);
        user.setConfirmed(true);
        user.setUserPermissions(Collections.emptyList());
        user.setSocialCommunication(true);
    }

    @Test
    void saveNewUserShouldReturnUser() {
        var result = userService.save(user);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveUpdateUserShouldReturnUser() {
        user.setId(1L);
        var result = userService.save(user);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewUserShouldThrowException() {
        user.setPassword(null);
        assertThrows(ServiceException.class,
            () -> {
                userService.save(user);
            }
        );
    }

    @Test
    void deleteUserShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                userService.delete(42L);
            }
        );
    }

    @Test
    void deleteUserShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                userService.delete(2L);
            }
        );
    }

    @Test
    void deleteAllUsersShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                userService.deleteAll(List.of(1L, 2L, 3L));
            }
        );
    }

    @Test
    void deleteAllUsersShouldThrowException() {
        var list = List.of(1L, 42L, 3L);
        assertThrows(ServiceException.class,
            () -> {
                userService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = userService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllUsers() {
        var result = userService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnUsers() {
        var result = userService.findAllById(List.of(1L, 3L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeUsers() {
        var result = userService.findAllById(List.of(1L, 42L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllUsers() {
        var filter = new GenericFilterDTO();
        var result = userService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeUsers() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Gabriel");
        filter.setQueryFields(List.of("name"));
        var result = userService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnUser() {
        var result = userService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = userService.findOne(42L);
        assertNull(result);
    }

    @Test
    void findOneByGoogleShouldReturnUser() {
        var result = userService.findByGoogleId("123456");
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void findOneByGoogleShouldReturnNull() {
        var result = userService.findByGoogleId("123");
        assertNull(result);
    }

    @Test
    void findOneByGithubShouldReturnUser() {
        var result = userService.findByGithubId("789");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneByGithubShouldReturnNull() {
        var result = userService.findByGithubId("456");
        assertNull(result);
    }

    @Test
    void findByEmailShouldReturnUser() {
        var result = userService.findByEmail("rafael@gmail.teste.com");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByEmailShouldReturnNull() {
        var result = userService.findByEmail("rafaelss@gmail.teste.com");
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnUser() {
        var result = userService.findByName("Default Admin");
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = userService.findByName("Mateus");
        assertNull(result);
    }

    @Test
    void findByTagNameShouldReturnUser() {
        var result = userService.findByTagName("Gabriel");
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByTagNameShouldReturnNull() {
        var result = userService.findByName("Gabriel2");
        assertNull(result);
    }

}
