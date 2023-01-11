package celtab.swge.controller;

import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {"classpath:db.scripts/users_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InfoControllerTest extends GenericTestController {

    public InfoControllerTest() {
        baseURL = "/api/info";
    }

    @Value("${project.version}")
    private String version;

    @Test
    void getVersionShouldReturnOk() {
        findShouldReturnStatusAndBody(baseURL + "/version", HttpStatus.OK)
            .jsonPath("$")
            .isEqualTo(version);
    }
}
