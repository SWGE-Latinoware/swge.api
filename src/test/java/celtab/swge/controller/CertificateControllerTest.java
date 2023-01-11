package celtab.swge.controller;

import celtab.swge.dto.CertificateRequestDTO;
import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.FileRequestDTO;
import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.service.CertificateService;
import celtab.swge.service.TrackService;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/tutored_user_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/dynamic_content_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/tutored_registration_data.sql",
    "classpath:db.scripts/registration_data.sql",
    "classpath:db.scripts/speaker_activity_data.sql",
    "classpath:db.scripts/individual_registration_data.sql",
    "classpath:db.scripts/tutored_individual_registration_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CertificateControllerTest extends GenericTestController {

    private CertificateRequestDTO certificateRequestDTO;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    public CertificateControllerTest() {
        baseURL = "/api/certificates";
    }

    @BeforeEach
    private void init() throws IOException {
        var editionRequestDTO = new EditionRequestDTO();
        editionRequestDTO.setId(3L);
        var background = new FileRequestDTO();
        background.setId(2L);
        certificateRequestDTO = new CertificateRequestDTO();
        certificateRequestDTO.setName("Certificado para o workshop de Criptografia");
        certificateRequestDTO.setBackgroundImage(background);
        certificateRequestDTO.setAvailabilityDateTime(new Date());
        certificateRequestDTO.setEdition(editionRequestDTO);
        certificateRequestDTO.setDynamicContents(Collections.emptyList());
        certificateRequestDTO.setAllowQrCode(true);

        var backgroundImg = new ClassPathResource("/images/background-certificate-empty.jpg").getFile();
        FileUtils.forceMkdir(new File(fileStorageProperties.getUploadDir()));
        FileCopyUtils.copy(backgroundImg, new File(fileStorageProperties.getUploadDir() + File.separator + "2"));
    }

    @Test
    void createShouldReturnOk() {
        createShouldReturnStatus(certificateRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void createShouldReturnBadRequest() {
        certificateRequestDTO.setName(null);
        createShouldReturnStatus(certificateRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldReturnOk() {
        certificateRequestDTO.setId(1L);
        updateShouldReturnStatus(certificateRequestDTO, baseURL, HttpStatus.OK);
    }

    @Test
    void updateShouldReturnBadRequest() {
        certificateRequestDTO.setId(21L);
        updateShouldReturnStatus(certificateRequestDTO, baseURL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOneShouldReturnCertificate() {
        findShouldReturnStatusAndBody(baseURL + "/1", HttpStatus.OK)
            .jsonPath("$.id").isEqualTo(1L);
    }

    @Test
    void findOneShouldReturnNotFound() {
        findShouldReturnStatusAndBody(baseURL + "/21", HttpStatus.NOT_FOUND);
    }

//    @Test
//    void deleteShouldReturnOK() {
//        deleteShouldReturnStatus(baseURL + "/1", HttpStatus.OK);
//    }

    @Test
    void deleteShouldReturnConflict() {
        deleteShouldReturnStatus(baseURL + "/21", HttpStatus.CONFLICT);
    }

//    @Test
//    void deleteAllShouldReturnOk() {
//        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("1", "2"))), HttpStatus.OK);
//    }

    @Test
    void deleteAllShouldReturnConflict() {
        deleteShouldReturnStatus(new URIObject(baseURL, Map.of("ids", List.of("21", "10"))), HttpStatus.CONFLICT);
    }

    @Test
    void filterShouldReturnCertificate() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Arduino");
        filter.setQueryFields(List.of("name"));
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/filter",
                Map.of("filter", List.of(getURLEncodedValue(filter)))
            ),
            HttpStatus.OK
        )
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void validateUniqueNameShouldReturnTrue() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/edition/name",
                Map.of("name", List.of("Certificado para o workshop de Arduino"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(true);
    }

    @Test
    void validateUniqueNameShouldReturnFalse() {
        findShouldReturnStatusAndBody(
            new URIObject(
                baseURL + "/unique/edition/name",
                Map.of("name", List.of("Certificado"), "editionId", List.of("1"))
            ),
            HttpStatus.OK
        )
            .jsonPath("$").isEqualTo(false);
    }

    @Test
    void generatePDFShouldReturnOk() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void generatePDFShouldReturnOkWithoutTrack() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void generatePDFShouldReturnOkWithoutActivity() {
        var track = trackService.findOne(1L);
        var attendeeCert = certificateService.findOne(1L);
        var speakerCert = certificateService.findOne(2L);
        track.setAttendeeCertificate(attendeeCert);
        track.setSpeakerCertificate(speakerCert);
        trackService.save(track);

        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"))
            ),
            HttpStatus.OK
        );
    }


    @Test
    void generatePDFShouldReturnOkSpeakerCertificate() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/2",
                Map.of("isTutored", List.of("false"), "userId", List.of("2"), "trackId", List.of("2"), "activityId", List.of("2"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void generatePDFShouldReturnNotFoundCertificate() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/10",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestCertificateNotAvailable() {

        var certificate = certificateService.findOne(1L);
        certificate.setAvailabilityDateTime(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        certificateService.save(certificate);

        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnNotFoundUser() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("5"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void generatePDFShouldReturnNotFoundTrackAndActivity() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("10"), "activityId", List.of("10"))
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestUserNotInTrack() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("2"), "activityId", List.of("1"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestActivityNotInTrack() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("2"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestUserNotRegistredInActivity() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("2"), "activityId", List.of("2"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnBadRequest() {
        var certificate = certificateService.findOne(1L);
        certificate.getBackgroundImage().setId(3L);
        certificateService.save(certificate);

        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("false"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnOkTutoredUser() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("true"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.OK
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestTutoredUser() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/1",
                Map.of("isTutored", List.of("true"), "userId", List.of("1"), "activityId", List.of("2"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void generatePDFShouldReturnBadRequestCertificateNotInTrackOrActivity() {
        findShouldReturnStatus(
            new URIObject(
                baseURL + "/pdf/3",
                Map.of("isTutored", List.of("true"), "userId", List.of("1"), "trackId", List.of("1"), "activityId", List.of("1"))
            ),
            HttpStatus.BAD_REQUEST
        );
    }
}
