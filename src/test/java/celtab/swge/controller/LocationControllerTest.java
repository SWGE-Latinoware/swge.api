package celtab.swge.controller;

import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {"classpath:db.scripts/users_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LocationControllerTest extends GenericTestController {

    public LocationControllerTest() {
        baseURL = "/api/location";
    }

    @Test
    void findUFShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/ufs", HttpStatus.OK);
    }

    @Test
    void findCitiesShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/cities", HttpStatus.OK);
    }
}
