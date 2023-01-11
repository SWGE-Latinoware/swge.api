package celtab.swge.controller;

import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FontControllerTest extends GenericTestController {

    public FontControllerTest() {
        baseURL = "/api/fonts";
    }

    @Test
    void getFontShouldReturnOk() {
        findShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("fileName", List.of("AppleStormCBo.otf"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void getFontShouldReturnNotFound() {
        findShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("fileName", List.of("AppleStormCBold.ttf"))
            ),
            HttpStatus.NOT_FOUND
        );
    }
}
