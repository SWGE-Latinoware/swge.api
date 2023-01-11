package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Edition;
import celtab.swge.model.UserPermission;
import celtab.swge.model.enums.UserRole;
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
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/user_permission_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserPermissionServiceTest extends GenericTestService {

    private UserPermission userPermission;

    @Autowired
    private UserPermissionService userPermissionService;

    @BeforeEach
    public void init() {
        var user = new User();
        user.setId(2L);
        var edition = new Edition();
        edition.setId(2L);
        userPermission = new UserPermission();
        userPermission.setUserRole(UserRole.ATTENDEE);
        userPermission.setEdition(edition);
        userPermission.setUser(user);
    }

    @Test
    void saveNewUserPermissionShouldReturn() {
        var result = userPermissionService.save(userPermission);
        assertNotNull(result);
        assertEquals(7L, result.getId());
    }

    @Test
    void updateUserPermissionShouldReturn() {
        userPermission.setId(1L);
        var result = userPermissionService.save(userPermission);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewUserPermissionShouldThrowException() {
        userPermission.setUserRole(null);
        assertThrows(ServiceException.class,
            () -> userPermissionService.save(userPermission)
        );
    }

    @Test
    void deleteUserPermissionShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> userPermissionService.delete(21L)
        );
    }

    @Test
    void deleteUserPermissionShouldNotThrowException() {
        assertDoesNotThrow(
            () -> userPermissionService.delete(1L)
        );
    }

    @Test
    void deleteAllUserPermissionShouldNotThrowException() {
        assertDoesNotThrow(
            () -> userPermissionService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllUserPermissionShouldThrowException() {
        var list = List.of(21L, 22L);
        assertThrows(ServiceException.class,
            () -> userPermissionService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableUserPermissionShouldReturnPage0() {
        var result = userPermissionService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllUserPermissions() {
        var result = userPermissionService.findAll();
        assertNotNull(result);
        assertEquals(6, result.size());
    }

    @Test
    void findAllByIdShouldReturnUserPermissions() {
        var result = userPermissionService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeUserPermission() {
        var result = userPermissionService.findAllById(List.of(2L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllUserPermissions() {
        var filter = new GenericFilterDTO();
        var result = userPermissionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeUserPermission() {
        var filter = new GenericFilterDTO();
        filter.setQuery("1");
        filter.setQueryFields(List.of("userRole"));
        var result = userPermissionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnUserPermission() {
        var result = userPermissionService.findOne(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findOneShouldReturnNullUserPermission() {
        var result = userPermissionService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findAllByEditionIdAndUserId() {
        var result = userPermissionService.findAllByEditionAndUser(2L, 2L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByEditionAndUserRoleShouldReturnList() {
        var result = userPermissionService.findAllByEditionAndUserRole(2L, UserRole.SPEAKER);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByEditionAndUserRoleAndUserShouldReturnList() {
        var result = userPermissionService.findByEditionAndUserRoleAndUser(2L, UserRole.SPEAKER, 2L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
