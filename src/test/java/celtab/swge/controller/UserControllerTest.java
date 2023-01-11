package celtab.swge.controller;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.UserRequestDTO;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Sql(scripts = {"classpath:db.scripts/users_data.sql", "classpath:db.scripts/registration_type_data.sql", "classpath:db.scripts/institution_data.sql", "classpath:db.scripts/edition_data.sql", "classpath:db.scripts/user_permission_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTest extends GenericTestController {

    private UserRequestDTO userRequestDTO;

    public UserControllerTest() {
        baseURL = "/api/users";
    }

    @BeforeEach
    private void init() {
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Mateus dos Santos");
        userRequestDTO.setEmail("mateus@gmail.teste.pti.com");
        userRequestDTO.setCountry("US");
        userRequestDTO.setZipCode("80052378");
        userRequestDTO.setState("ES");
        userRequestDTO.setCity("San Francisco");
        userRequestDTO.setAddressLine1("5ª Avenida");
        userRequestDTO.setPassword("pti1234");
        userRequestDTO.setTagName("mateus");
        userRequestDTO.setAdmin(false);
        userRequestDTO.setEmailCommunication(true);
        userRequestDTO.setEnabled(true);
        userRequestDTO.setConfirmed(true);
        userRequestDTO.setUserPermissions(Collections.emptyList());
        userRequestDTO.setSocialCommunication(true);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        userRequestDTO.setName(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByCountryBlank() {
        userRequestDTO.setCountry("");
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByCountryNull() {
        userRequestDTO.setCountry(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByZipCodeNull() {
        userRequestDTO.setZipCode(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByZipCodeBlank() {
        userRequestDTO.setZipCode("");
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByStateBlank() {
        userRequestDTO.setState("");
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByStateNull() {
        userRequestDTO.setState(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByCityNull() {
        userRequestDTO.setCity(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByCityBlank() {
        userRequestDTO.setCity("");
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByAddressLineBlank() {
        userRequestDTO.setAddressLine1("");
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByAddressLineNull() {
        userRequestDTO.setAddressLine1(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnBadRequestByEmailNull() {
        userRequestDTO.setEmail(null);
        createShouldReturnStatus(userRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAutoShouldReturnOk() {
        createShouldReturnOk();
    }

    @Test
    void createAutoShouldReturnBadRequest() {
        createShouldReturnBadRequest();
    }

    @Test
    void autoRegistrationShouldReturnOk() {
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/auto-registration",
            HttpStatus.OK
        );
    }

    @Test
    void autoRegistrationShouldReturnBadRequest() {
        userRequestDTO.setPassword(null);
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/auto-registration",
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void resetPasswordShouldReturnOk() {
        createShouldReturnStatus(
            userRequestDTO,
            new URIObject(
                baseURL + "/reset-password",
                Map.of("email", List.of("gabriel@gmail.teste.pti.com"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void resetPasswordShouldReturnNotFound() {
        createShouldReturnStatus(
            userRequestDTO,
            new URIObject(
                baseURL + "/reset-password",
                Map.of("email", List.of("gabrielaaa@gmail.teste.pti.com"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void updateByAdminShouldReturnOk() {
        userRequestDTO.setId(2L);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/admin", HttpStatus.OK);
    }

    @Test
    void updateByAdminShouldReturnNotFound() {
        userRequestDTO.setId(100L);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/admin", HttpStatus.NOT_FOUND);
    }

    @Test
    void updateByAdminShouldReturnBadRequest() {
        userRequestDTO.setId(3L);
        userRequestDTO.setEmail(null);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/admin", HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateByUserShouldReturnOk() {
        userRequestDTO.setId(3L);
        userRequestDTO.setPassword(null);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/user", HttpStatus.OK);
    }

    @Test
    void updateByUserShouldReturnForbidden() {
        userRequestDTO.setId(100L);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/user", HttpStatus.FORBIDDEN);
    }

    @Test
    void updateByUserShouldReturnBadRequest() {
        userRequestDTO.setId(3L);
        userRequestDTO.setCountry(null);
        updateShouldReturnStatus(userRequestDTO, baseURL + "/user", HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteAllShouldAlwaysReturnInternetServerError() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "2"))
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    void deleteShouldAlwaysReturnInternetServerError() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void findAllShouldReturnPage0() {
        findShouldReturnStatusAndBody(baseURL, HttpStatus.OK)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(3);
    }

    @Test
    void filterShouldReturnPage0() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(new GenericFilterDTO())))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(3);
    }

    @Test
    void filterShouldReturnUser2() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Gabriel");
        filter.setQueryFields(List.of("name"));
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(filter)))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/42", HttpStatus.NOT_FOUND);
    }

    @Test
    void findOneShouldReturnUser() {
        findShouldReturnStatusAndBody(baseURL + "/2", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(2L);
    }

    @Test
    void changeEnabledShouldReturnOk() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/change-enable/1", HttpStatus.OK);
    }

    @Test
    void changeEnabledShouldReturnNotFound() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/change-enable/100", HttpStatus.NOT_FOUND);
    }

    @Test
    void untieSocialAccountShouldReturnOk() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/social-login/untie/google/3", HttpStatus.OK);
    }

    @Test
    void untieSocialAccountShouldReturnOkGithub() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/social-login/untie/github/1", HttpStatus.OK);
    }

    @Test
    void untieSocialAccountShouldReturnBadRequestByRegistration() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/social-login/untie/lattes/3", HttpStatus.BAD_REQUEST);
    }

    @Test
    void untieSocialAccountShouldReturnNotFound() {
        updateShouldReturnStatus(userRequestDTO, baseURL + "/social-login/untie/google/999", HttpStatus.NOT_FOUND);
    }

    @Test
    void validateUniqueEmailShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/email",
                Map.of("email", List.of("admin@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueEmailShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/email",
                Map.of("email", List.of("admin2@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("Rafael Braz"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/name",
                Map.of("name", List.of("Rafael S. Braz"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUniqueTagNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/tag-name",
                Map.of("tagName", List.of("Admin"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }


    @Test
    void validateUniqueTagNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/tag-name",
                Map.of("tagName", List.of("Admin2"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void resendConfirmationByEmailShouldReturnOK() {
        createShouldReturnStatus(
            userRequestDTO,
            new URIObject(
                baseURL + "/email-confirmation/email",
                Map.of("email", List.of("admin@gmail.teste.com"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void resendConfirmationByEmailShouldReturnNotFound() {
        createShouldReturnStatus(
            userRequestDTO,
            new URIObject(
                baseURL + "/email-confirmation/email",
                Map.of("email", List.of("admin.rafael@gmail.teste.com"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void validateConfirmationShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/validate/email-confirmation",
                Map.of("email", List.of("admin@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateConfirmationShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/validate/email-confirmation",
                Map.of("email", List.of("gabriel@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateConfirmationShouldReturnNotFound() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/validate/email-confirmation",
                Map.of("email", List.of("gabrielasas@gmail.teste.com"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void validateUserDisabledShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/validate/user-disabled",
                Map.of("email", List.of("rafael@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUserDisabledShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/validate/user-disabled",
                Map.of("email", List.of("gabriel@gmail.teste.com"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void validateUserDisabledShouldReturnNotFound() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/validate/user-disabled",
                Map.of("email", List.of("gabrielasas@gmail.teste.com"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void resendConfirmationByIdShouldReturnOK() {
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/email-confirmation/1",
            HttpStatus.OK
        );
    }

    @Test
    void resendConfirmationByIdShouldReturnNotFound() {
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/email-confirmation/5",
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void findOneByEmailShouldReturnUser() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/email",
                Map.of("email", List.of("gabriel@gmail.teste.com"))
            ), HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(2L);
    }

    @Test
    void findOneByEmailShouldReturnNotFound() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/email",
                Map.of("email", List.of("gabriela@gmail.teste.com"))
            ), HttpStatus.NOT_FOUND);
    }

    @Test
    void resendEmailShouldReturnOk() {
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/email-confirmation/1",
            HttpStatus.OK
        );
    }

    @Test
    void resendEmailShouldReturnNotFound() {
        createShouldReturnStatus(
            userRequestDTO,
            baseURL + "/email-confirmation/999",
            HttpStatus.NOT_FOUND
        );
    }
}
