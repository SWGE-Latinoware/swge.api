package celtab.swge.controller;

import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.UserPermissionRequestDTO;
import celtab.swge.dto.UserRequestDTO;
import celtab.swge.model.enums.UserRole;
import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/user_permission_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserPermissionControllerTest extends GenericTestController {

    private UserPermissionRequestDTO userPermissionRequestDTO;

    public UserPermissionControllerTest() {
        baseURL = "/api/user-permissions";
    }

    @BeforeEach
    private void init() {
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        userPermissionRequestDTO = new UserPermissionRequestDTO();
        userPermissionRequestDTO.setUserRole(UserRole.ATTENDEE);
        userPermissionRequestDTO.setUser(userRequestDTO);
        userPermissionRequestDTO.setEdition(editionRequestDTO);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(userPermissionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        userPermissionRequestDTO.setUserRole(null);
        createShouldReturnStatus(userPermissionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        userPermissionRequestDTO.setId(1L);
        updateShouldReturnStatus(userPermissionRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        userPermissionRequestDTO.setId(21L);
        updateShouldReturnStatus(userPermissionRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

    @Test
    void findAllShouldReturnEditionPage() {
        findShouldReturnStatus(baseURL + "/edition/2/user-permissions/2", HttpStatus.OK);
    }

    @Test
    void findOneShouldReturnUserPermission() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatusAndBody(baseURL + "/21", HttpStatus.NOT_FOUND);
    }
}
