package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Certificate;
import celtab.swge.model.Edition;
import celtab.swge.model.File;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/certificate_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CertificateServiceTest extends GenericTestService {

    private Certificate certificate;

    @Autowired
    private CertificateService certificateService;

    @BeforeEach
    private void init() {
        var edition = new Edition();
        edition.setId(1L);
        var background = new File();
        background.setId(2L);
        certificate = new Certificate();
        certificate.setName("Certificado para o workshop de segurança digital");
        certificate.setBackgroundImage(background);
        certificate.setAvailabilityDateTime(new Date());
        certificate.setEdition(edition);
        certificate.setDynamicContents(Collections.emptyList());
        certificate.setAllowQrCode(true);
    }

    @Test
    void saveNewShouldReturnCertificate() {
        var result = certificateService.save(certificate);
        assertNotNull(result);
        assertEquals(4, result.getId());

    }

    @Test
    void saveUpdateCertificateShouldReturnCertificate() {
        certificate.setId(1L);
        var result = certificateService.save(certificate);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewCertificateShoutThrowException() {
        certificate.setName(null);
        assertThrows(ServiceException.class,
            () -> certificateService.save(certificate)
        );
    }

    @Test
    void deleteCertificateShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> certificateService.delete(21L)
        );
    }

    @Test
    void deleteCertificateShouldNotThrowException() {
        assertDoesNotThrow(
            () -> certificateService.delete(1L)
        );
    }

    @Test
    void deleteAllCertificateShouldNotThrowException() {
        assertDoesNotThrow(
            () -> certificateService.deleteAll(List.of(1L, 2L))
        );

    }

    @Test
    void deleteAllCertificateShouldThrowException() {
        var list = List.of(21L, 10L);
        assertThrows(ServiceException.class,
            () -> certificateService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableCertificateShouldReturnPage0() {
        var result = certificateService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnCertificates() {
        var result = certificateService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnCertificates() {
        var result = certificateService.findAllById(List.of(2L, 3L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeCertificates() {
        var result = certificateService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllCertificate() {
        var filter = new GenericFilterDTO();
        var result = certificateService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());

    }

    @Test
    void filterShouldReturnSomeCertificates() {
        var filter = new GenericFilterDTO();
        filter.setQuery("IoT");
        filter.setQueryFields(List.of("name"));
        var result = certificateService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnCertificates() {
        var result = certificateService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullCertificate() {
        var result = certificateService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnCertificate() {
        var result = certificateService.findByNameAndEdition("Certificado para o workshop de Arduino", 1L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = certificateService.findByNameAndEdition("Certificado para o workshop de Arduinos", 1L);
        assertNull(result);
    }

    @Test
    void findAllByEditionShouldReturnList() {
        var result = certificateService.findAllByEdition(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
