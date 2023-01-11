package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Edition;
import celtab.swge.model.Institution;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/user_permission_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/registration_data.sql",
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/speaker_activity_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EditionServiceTest extends GenericTestService {

    private Edition edition;

    @Autowired
    private EditionService editionService;

    @BeforeEach
    private void init() {
        edition = new Edition();
        edition.setEnabled(true);
        edition.setName("21 edicao");
        edition.setShortName("21");
        edition.setYear(2021);
        edition.setInitialDate(new Date());
        edition.setFinalDate(Date.from(LocalDate.now().plusDays(3).atStartOfDay().toInstant(ZoneOffset.UTC)));
        edition.setType(EditionType.ONLINE);
        var institution = new Institution();
        institution.setId(1L);
        edition.setInstitution(institution);
    }

    @Test
    void saveNewEditionShouldReturnEdition() {
        var result = editionService.save(edition);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveUpdateEditionShouldReturnEdition() {
        edition.setId(1L);
        var result = editionService.save(edition);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewEditionShouldThrowException() {
        edition.setName(null);
        assertThrows(ServiceException.class,
            () -> editionService.save(edition)
        );
    }

    @Test
    void deleteShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> editionService.delete(52L)
        );
    }

//    @Test
//    void deleteEditionShouldNotThrowException() {
//        assertDoesNotThrow(
//            () -> editionService.delete(1L)
//        );
//    }
//
//    @Test
//    void deleteAllEditionsShouldNotThrowException() {
//        assertDoesNotThrow(
//            () -> editionService.deleteAll(List.of(1L, 3L))
//        );
//    }

    @Test
    void deleteAllEditionsShouldThrowException() {
        var list = List.of(1L, 42L);
        assertThrows(ServiceException.class,
            () -> editionService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableShouldReturnPage0() {
        var result = editionService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnAllEditions() {
        var result = editionService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnEditions() {
        var result = editionService.findAllById(List.of(1L, 2L, 3L));
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeEditions() {
        var result = editionService.findAllById(List.of(1L, 34L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllEditions() {
        var filter = new GenericFilterDTO();
        var result = editionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeEdition() {
        var filter = new GenericFilterDTO();
        filter.setQuery("18 edicao");
        filter.setQueryFields(List.of("name"));
        var result = editionService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnEdition() {
        var result = editionService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNull() {
        var result = editionService.findOne(42L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnEdition() {
        var result = editionService.findByName("18 edicao");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = editionService.findByName("180 edicao");
        assertNull(result);
    }

    @Test
    void findByShortNameShouldReturnEdition() {
        var result = editionService.findByShortName("18");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByShortNameShouldReturnNull() {
        var result = editionService.findByShortName("180");
        assertNull(result);
    }

    @Test
    void findByYearShouldReturnEdition() {
        var result = editionService.findByYear(2019);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByYearShouldReturnNull() {
        var result = editionService.findByYear(1912);
        assertNull(result);
    }

    @Test
    void findAllSpeakersShouldReturnOk() {
        var result = editionService.findAllSpeakers(2L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllCertificatesShouldReturnOk() {
        var result = editionService.findAllCertificates(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllTracksShouldReturnList() {
        var result = editionService.findAllTracks(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllActivitiesBySpeakerShouldReturnOk() {
        var result = editionService.findAllActivitiesBySpeaker(2L, 2L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllActivitiesBySpeakerShouldReturnNull() {
        var result = editionService.findAllActivitiesBySpeaker(1L, 3L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
