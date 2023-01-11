package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Activity;
import celtab.swge.model.SpeakerActivity;
import celtab.swge.model.user.User;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

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
    "classpath:db.scripts/activity_data.sql",
    "classpath:db.scripts/speaker_activity_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SpeakerActivityServiceTest extends GenericTestService {

    private SpeakerActivity speakerActivity;

    @Autowired
    private SpeakerActivityService speakerActivityService;

    @BeforeEach
    private void init() {
        var activity = new Activity();
        activity.setId(1L);
        var user = new User();
        user.setId(1L);
        speakerActivity = new SpeakerActivity();
        speakerActivity.setActivity(activity);
        speakerActivity.setSpeaker(user);
    }

    @Test
    void saveNewSpeakerActivityShouldReturnSpeakerActivity() {
        var result = speakerActivityService.save(speakerActivity);
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void saveUpdateSpeakerActivityShouldReturnSpeakerActivity() {
        speakerActivity.setId(1L);
        var result = speakerActivityService.save(speakerActivity);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewSpeakerActivityShouldThrowException() {
        speakerActivity.setSpeaker(null);
        assertThrows(ServiceException.class,
            () -> speakerActivityService.save(speakerActivity)
        );
    }

    @Test
    void deleteSpeakerActivityShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> speakerActivityService.delete(31L));
    }

    @Test
    void deleteSpeakerActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> speakerActivityService.delete(1L)
        );
    }

    @Test
    void deleteAllSpeakerActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> speakerActivityService.deleteAll(List.of(1L, 2L))
        );
    }

    @Test
    void deleteAllSpeakerActivityShouldThrowException() {
        var list = List.of(13L, 4L);
        assertThrows(ServiceException.class,
            () -> speakerActivityService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableSpeakerActivityShouldReturnPage0() {
        var result = speakerActivityService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllShouldReturnSpeakerActivities() {
        var result = speakerActivityService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSpeakerActivities() {
        var result = speakerActivityService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2L, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeSpeakerActivities() {
        var result = speakerActivityService.findAllById(List.of(2L, 32L));
        assertNotNull(result);
        assertEquals(1L, result.size());
    }

    @Test
    void filterShouldReturnAllSpeakerActivities() {
        var filter = new GenericFilterDTO();
        var result = speakerActivityService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2L, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeSpeakerActivities() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("activity.id"));
        var result = speakerActivityService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1L, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnSpeakerActivity() {
        var result = speakerActivityService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullSpeakerActivity() {
        var result = speakerActivityService.findOne(23L);
        assertNull(result);
    }

    @Test
    void deleteAllByActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> speakerActivityService.deleteAllByActivity(3L)
        );
    }

    @Test
    void deleteAllByExistingActivityShouldNotThrowException() {
        assertDoesNotThrow(
            () -> speakerActivityService.deleteAllByActivity(1L)
        );
    }

    @Test
    void findAllByEditionAndSpeakerShouldReturnList() {
        var result = speakerActivityService.findAllByEditionAndSpeaker(1L, 2L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
