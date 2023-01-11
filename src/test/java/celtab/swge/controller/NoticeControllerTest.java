package celtab.swge.controller;

import celtab.swge.dto.CaravanRequestDTO;
import celtab.swge.dto.NoticeRequestDTO;
import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/caravan_data.sql",
    "classpath:db.scripts/notice_data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class NoticeControllerTest extends GenericTestController {


    private NoticeRequestDTO noticeRequestDTO;

    public NoticeControllerTest() {
        baseURL = "/api/notices";
    }

    @BeforeEach
    private void init() {
        var caravanRequestDTO = new CaravanRequestDTO();
        caravanRequestDTO.setId(1L);
        noticeRequestDTO = new NoticeRequestDTO();
        noticeRequestDTO.setCaravan(caravanRequestDTO);
        noticeRequestDTO.setDateTime(new Date());
        noticeRequestDTO.setDescription(Map.of("teste1", "teste01", "teste2", "teste02"));
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(noticeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        noticeRequestDTO.setDateTime(null);
        createShouldReturnStatus(noticeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        noticeRequestDTO.setId(1L);
        updateShouldReturnStatus(noticeRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        noticeRequestDTO.setId(21L);
        updateShouldReturnStatus(noticeRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/3", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }
}
