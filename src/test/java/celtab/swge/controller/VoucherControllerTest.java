package celtab.swge.controller;

import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.VoucherRequestDTO;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@Sql(scripts = {
        "classpath:db.scripts/users_data.sql",
        "classpath:db.scripts/registration_type_data.sql",
        "classpath:db.scripts/institution_data.sql",
        "classpath:db.scripts/edition_data.sql",
        "classpath:db.scripts/track_data.sql",
        "classpath:db.scripts/voucher_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class VoucherControllerTest extends GenericTestController {

    private VoucherRequestDTO voucherRequestDTO;

    public VoucherControllerTest() {
        baseURL = "/api/vouchers";
    }

    @BeforeEach
    private void init() {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(2L);
        voucherRequestDTO = new VoucherRequestDTO();
        voucherRequestDTO.setEdition(editionRequestDTO);
        voucherRequestDTO.setUserEmail("");
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(voucherRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(9L);
        voucherRequestDTO.setEdition(editionRequestDTO);
        createShouldReturnStatus(voucherRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldReturnConflict() {
        voucherRequestDTO.setUserEmail("rafael@gmail.teste.com");
        createShouldReturnStatus(voucherRequestDTO, baseURL, HttpStatus.CONFLICT);
    }

    @Test
    void updateShouldReturnOk() {
        voucherRequestDTO.setId(3L);
        updateShouldReturnStatus(voucherRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        voucherRequestDTO.setId(21L);
        updateShouldReturnStatus(voucherRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnTrack() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/2", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("2", "3"))), HttpStatus.OK);
    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "22"))), HttpStatus.CONFLICT);
    }
}
