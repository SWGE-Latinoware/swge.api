package celtab.swge.controller;


import celtab.swge.dto.FeedbackRequestDTO;
import celtab.swge.dto.FileRequestDTO;
import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.dto.UserRequestDTO;
import celtab.swge.model.enums.FeedbackStatus;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.util.GenericTestController;
import celtab.swge.util.URIObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/feedback_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FeedbackControllerTest extends GenericTestController {

    private FeedbackRequestDTO feedbackRequestDTO;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    public FeedbackControllerTest() {
        baseURL = "/api/feedbacks";
    }

    @BeforeEach
    private void init() throws IOException {
        var cities = new ClassPathResource("ibge/cities.json").getFile();
        FileUtils.forceMkdir(new File(fileStorageProperties.getUploadDir()));
        FileCopyUtils.copy(cities, new File(fileStorageProperties.getUploadDir() + File.separator + "1"));
        FileCopyUtils.copy(cities, new File(fileStorageProperties.getUploadDir() + File.separator + "2"));
        FileCopyUtils.copy(cities, new File(fileStorageProperties.getUploadDir() + File.separator + "3"));
        var fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setId(1L);
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(1L);
        feedbackRequestDTO = new FeedbackRequestDTO();
        feedbackRequestDTO.setTitle("Report Bugs");
        feedbackRequestDTO.setStatus(FeedbackStatus.OPEN);
        feedbackRequestDTO.setFile(fileRequestDTO);
        feedbackRequestDTO.setUser(userRequestDTO);
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(feedbackRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        feedbackRequestDTO.setTitle(null);
        createShouldReturnStatus(feedbackRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        feedbackRequestDTO.setId(3L);
        updateShouldReturnStatus(feedbackRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        feedbackRequestDTO.setId(21L);
        updateShouldReturnStatus(feedbackRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnTrack() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void deleteShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/42", HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteAllShouldReturnOk() {
        deleteShouldReturnStatus(
            new URIObject(
                baseURL,
                Map.of("ids", List.of("1", "2"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

    @Test
    void filterShouldReturnTrack() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Bug");
        filter.setQueryFields(List.of("title"));
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(filter)))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void findAllByListShouldReturnOk() {
        findShouldReturnStatus(
            baseURL + "/list",
            HttpStatus.OK
        );
    }
    
    @Test
    void findAllShouldReturnPage() {
        findShouldReturnStatus(baseURL, HttpStatus.OK);
    }
}
