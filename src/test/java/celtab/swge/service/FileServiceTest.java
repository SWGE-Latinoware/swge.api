package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.File;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FileServiceTest extends GenericTestService {

    private File file;

    private java.io.File resource;

    private InputStream resourceInputStream;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @BeforeEach
    private void init() throws IOException {
        file = new File();
        file.setName("file_test");
        file.setFormat("json");

        resource = new ClassPathResource("ibge/ufs.json").getFile();
        resourceInputStream = new ClassPathResource("ibge/ufs.json").getInputStream();

        var cities = new ClassPathResource("ibge/cities.json").getFile();
        FileUtils.forceMkdir(new java.io.File(fileStorageProperties.getUploadDir()));
        FileCopyUtils.copy(cities, new java.io.File(fileStorageProperties.getUploadDir() + java.io.File.separator + "1"));
    }

    @Test
    void saveNewFileShouldReturnFile() throws IOException {
        var result = fileService.saveFile(file, new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, new FileInputStream(resource)));
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveNewFileShouldThrowException() throws IOException {
        file.setName(null);
        var multiPart = new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, new FileInputStream(resource));
        assertThrows(ServiceException.class,
            () -> fileService.saveFile(file, multiPart)
        );
    }

    @Test
    void saveUpdateFileShouldReturnFile() throws IOException {
        file.setId(1L);
        var result = fileService.saveFile(file, new MockMultipartFile("file", file.getName(), MediaType.APPLICATION_JSON_VALUE, new FileInputStream(resource)));
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveFileByBlobShouldReturnFile() {
        var result = fileService.saveFile(file, resourceInputStream);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveFileByBlobShouldThrowException() {
        file.setName(null);
        assertThrows(ServiceException.class,
            () -> fileService.saveFile(file, resourceInputStream)
        );
    }

    @Test
    void saveFileByBlobWithIdShouldReturnFile() {
        file.setId(1L);
        var result = fileService.saveFile(file, resourceInputStream);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnFileAsResource() {
        var result = fileService.loadFileAsResource(1L);
        assertNotNull(result);
    }

    @Test
    void findOneShouldThrowExceptionFileAsResource() {
        assertThrows(ServiceException.class,
            () -> fileService.loadFileAsResource(100L)
        );
    }

    @Test
    void findOneShouldReturnFileWithResource() {
        var result = fileService.loadFileWithResource(1L);
        assertNotNull(result);
    }

    @Test
    void findOneShouldThrowExceptionFileWithResource() {
        assertThrows(ServiceException.class,
            () -> fileService.loadFileWithResource(100L)
        );
    }

    @Test
    void removeFileShouldReturnOk() {
        assertDoesNotThrow(
            () -> fileService.removeFile(1L)
        );
    }

    @Test
    void removeFileShouldReturnConflict() {
        assertThrows(ServiceException.class,
            () -> fileService.removeFile(21L)
        );
    }

    @Test
    void LoadFileWithResourceShouldThrowExceptionFileNotFound() {
        assertThrows(ServiceException.class,
            () -> fileService.loadFileWithResource(21L)
        );
    }
}
