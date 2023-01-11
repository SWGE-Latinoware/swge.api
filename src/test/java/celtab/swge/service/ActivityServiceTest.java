package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Activity;
import celtab.swge.model.Certificate;
import celtab.swge.model.Track;
import celtab.swge.model.enums.ActivityType;
import celtab.swge.model.enums.EditionType;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/file_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/track_data.sql",
    "classpath:db.scripts/certificate_data.sql",
    "classpath:db.scripts/activity_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ActivityServiceTest extends GenericTestService {

    private Activity activity;

    @Autowired
    private ActivityService activityService;

    @BeforeEach
    private void init() {
        var attendeeCertificate = new Certificate();
        attendeeCertificate.setId(1L);
        var speakerCertificate = new Certificate();
        speakerCertificate.setId(2L);
        var track = new Track();
        track.setId(1L);
        activity = new Activity();
        activity.setName("Mini Curso de git e github");
        activity.setLanguage("Português");
        activity.setVacancies(10);
        activity.setPrice(100.0);
        activity.setWorkload("01:00");
        activity.setType(ActivityType.MINI_COURSE);
        activity.setPresentationType(EditionType.ONLINE);
        activity.setTrack(track);
        activity.setAttendeeCertificate(attendeeCertificate);
        activity.setSpeakerCertificate(speakerCertificate);
        activity.setSpeakers(Collections.emptyList());
        activity.setSchedule(Collections.emptyList());
    }

    @Test
    void saveNewShouldReturnActivity() {
        var result = activityService.save(activity);
        assertNotNull(result);
        assertEquals(5, result.getId());
    }

    @Test
    void saveUpdateActivityShouldReturnActivity() {
        activity.setId(1L);
        var result = activityService.save(activity);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewShouldThrowException() {
        activity.setName(null);
        assertThrows(ServiceException.class,
            () -> activityService.save(activity)
        );
    }

    @Test
    void deleteActivityShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> activityService.delete(21L)
        );
    }

    @Test
    void deleteActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> activityService.delete(1L)
        );
    }

    @Test
    void deleteAllActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> activityService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllActivityShouldThrowException() {
        var list = List.of(21L, 22L);
        assertThrows(ServiceException.class,
            () -> activityService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableActivityShouldReturnPage0() {
        var result = activityService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnActivity() {
        var result = activityService.findAll();
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void findAllByIdShouldReturnActivity() {
        var result = activityService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2L, result.size());
    }

    @Test
    void findAllByEditionShouldReturnActivity() {
        var result = activityService.findAllByEdition(1L);
        assertNotNull(result);
        assertEquals(3L, result.size());
    }

    @Test
    void findAllLecturesByEditionShouldReturnEmpty() {
        var result = activityService.findAllLecturesByEdition(1L);
        assertNotNull(result);
        assertEquals(0L, result.size());
    }

    @Test
    void findAllLecturesByEditionShouldReturnActivity() {
        var result = activityService.findAllLecturesByEdition(2L);
        assertNotNull(result);
        assertEquals(1L, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeActivity() {
        var result = activityService.findAllById(List.of(21L, 2L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllActivity() {
        var filter = new GenericFilterDTO();
        var result = activityService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeActivity() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Apresentação");
        filter.setQueryFields(List.of("name"));
        var result = activityService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnActivity() {
        var result = activityService.findOne(1L);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findOneShouldReturnNullActivity() {
        var result = activityService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnActivity() {
        var result = activityService.findByNameAndEdition("Mini Curso de Arduino", 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = activityService.findByNameAndEdition("Mini Curso de Arduinos", 1L);
        assertNull(result);
    }
}
