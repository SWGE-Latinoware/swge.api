package celtab.swge.controller;

import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FluxControllerTest extends GenericTestController {

    private FluxControllerTest() {
        baseURL = "/api/flux";
    }

    @Test
    void fluxAllShouldReturnOk() {
        findShouldReturnStatus(
            baseURL + "/all",
            HttpStatus.OK
        );
    }
}
