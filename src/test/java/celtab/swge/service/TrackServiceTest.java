package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Edition;
import celtab.swge.model.Track;
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
    "classpath:db.scripts/track_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TrackServiceTest extends GenericTestService {

    private Track track;

    @Autowired
    private TrackService trackService;

    @BeforeEach
    private void init() {
        var edition = new Edition();
        edition.setId(1L);
        track = new Track();
        track.setName("Qualidade de software");
        track.setInitialDate(new Date());
        track.setFinalDate(Date.from(LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));
        track.setEdition(edition);
    }

    @Test
    void saveNewTrackShouldReturnTrack() {
        var result = trackService.save(track);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void saveUpdateTrackShouldReturnTrack() {
        track.setId(1L);
        var result = trackService.save(track);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewTrackShouldThrowException() {
        track.setName(null);
        assertThrows(ServiceException.class,
            () -> trackService.save(track)
        );
    }

    @Test
    void deleteTrackShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> trackService.delete(21L)
        );
    }

    @Test
    void deleteTrackShouldNotThrowException() {
        assertDoesNotThrow(
            () -> trackService.delete(1L)
        );
    }

    @Test
    void deleteAllTrackShouldNotThrowException() {
        assertDoesNotThrow(
            () -> trackService.deleteAll(List.of(1L, 3L))
        );
    }

    @Test
    void deleteAllTrackShouldThrowException() {
        var list = List.of(21L, 24L);
        assertThrows(ServiceException.class,
            () -> trackService.deleteAll(list)
        );
    }

    @Test
    void findAllPageableTrackShouldReturnPage0() {
        var result = trackService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnTracks() {
        var result = trackService.findAll();
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void findAllByIdShouldReturnTracks() {
        var result = trackService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeTracks() {
        var result = trackService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllTracks() {
        var filter = new GenericFilterDTO();
        var result = trackService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeTrack() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Segurança");
        filter.setQueryFields(List.of("name"));
        var result = trackService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnTrack() {
        var result = trackService.findOne(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findOneShouldReturnNullTrack() {
        var result = trackService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnTrack() {
        var result = trackService.findByNameAndEdition("Segurança da Informação", 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = trackService.findByNameAndEdition("Segurança", 1L);
        assertNull(result);
    }

    @Test
    void findAllByEditionShouldReturnList() {
        var result = trackService.findAllByEdition(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
