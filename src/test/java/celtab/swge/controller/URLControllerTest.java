package celtab.swge.controller;

import celtab.swge.service.URLService;
import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/url_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class URLControllerTest extends GenericTestController {

    @Autowired
    private URLService urlService;

    public URLControllerTest() {
        baseURL = "/api/url";
    }

    @Test
    void processURLShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/399b940a-3266-469c-8975-5a0b90ac9ea6", HttpStatus.OK);
    }

    @Test
    void processURLShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/399b940a-3266-469c-8975-5a0b90ac9ea1", HttpStatus.NOT_FOUND);
    }

    @Test
    void processURLShouldReturnConflict() {
        var urlData = urlService.findByURL("399b940a-3266-469c-8975-5a0b90ac9ea6");

        urlData.setEmail("usuarioo@gmail.com");
        urlService.save(urlData);

        findShouldReturnStatus(baseURL + "/399b940a-3266-469c-8975-5a0b90ac9ea6", HttpStatus.CONFLICT);
    }

}
