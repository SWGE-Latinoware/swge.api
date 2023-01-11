package celtab.swge.controller;

import celtab.swge.dto.FileRequestDTO;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.util.GenericTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FileControllerTest extends GenericTestController {

    private FileRequestDTO fileRequestDTO;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    public FileControllerTest() {
        baseURL = "/api/files";
    }

    @BeforeEach
    private void init() throws IOException {
        fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setName("file_test");
        fileRequestDTO.setFormat("json");

        var cities = new ClassPathResource("ibge/cities.json").getFile();
        FileUtils.forceMkdir(new File(fileStorageProperties.getUploadDir()));
        FileCopyUtils.copy(cities, new File(fileStorageProperties.getUploadDir() + File.separator + "1"));
    }

    @Test
    void createShouldReturnOk() {
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("name", fileRequestDTO.getName(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("format", fileRequestDTO.getFormat(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("file", new ClassPathResource("ibge/ufs.json"));
        createMultiPartShouldReturnStatus(BodyInserters.fromMultipartData(multipartBodyBuilder.build()), baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnOk() {
        fileRequestDTO.setId(1L);
        fileRequestDTO.setName("cities");
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("id", fileRequestDTO.getId());
        multipartBodyBuilder.part("name", fileRequestDTO.getName(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("format", fileRequestDTO.getFormat(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("file", new ClassPathResource("ibge/ufs.json"));
        updateMultiPartShouldReturnStatus(BodyInserters.fromMultipartData(multipartBodyBuilder.build()), baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        fileRequestDTO.setId(100L);
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("id", fileRequestDTO.getId());
        multipartBodyBuilder.part("name", fileRequestDTO.getName(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("format", fileRequestDTO.getFormat(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("file", new ClassPathResource("ibge/ufs.json"));
        updateMultiPartShouldReturnStatus(BodyInserters.fromMultipartData(multipartBodyBuilder.build()), baseURL, HttpStatus.NOT_FOUND);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/42", HttpStatus.NOT_FOUND);
    }

    @Test
    void findOneShouldReturnFile() {
        findShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteFileShouldReturnOk() {
        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
    }

    @Test
    void deleteFileShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/22", HttpStatus.CONFLICT);
    }

    @Test
    void getTermHTMLShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/terms/image-term", HttpStatus.OK);
    }

    @Test
    void getTermPDFShouldReturnOk() {
        findShouldReturnStatus(baseURL + "/terms/authorization-term", HttpStatus.OK);
    }

    @Test
    void getTermShouldReturnNotFound() {
        findShouldReturnStatus(baseURL + "/terms/images-term", HttpStatus.NOT_FOUND);
    }
}
