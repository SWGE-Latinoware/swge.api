package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Caravan;
import celtab.swge.model.Notice;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/users_data.sql",
    "classpath:db.scripts/institution_data.sql",
    "classpath:db.scripts/registration_type_data.sql",
    "classpath:db.scripts/edition_data.sql",
    "classpath:db.scripts/caravan_data.sql",
    "classpath:db.scripts/notice_data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class NoticeServiceTest extends GenericTestService {

    private Notice notice;

    @Autowired
    private NoticeService noticeService;

    @BeforeEach
    private void init() {
        var caravan = new Caravan();
        caravan.setId(1L);
        notice = new Notice();
        notice.setCaravan(caravan);
        notice.setDateTime(new Date());
        notice.setDescription(Map.of("teste1", "teste01", "teste2", "teste02"));
    }

    @Test
    void SaveNewNoticeShouldReturnNotice() {
        var result = noticeService.save(notice);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveUpdateNoticeShouldReturnNotice() {
        notice.setId(2L);
        var result = noticeService.save(notice);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void saveNewNoticeShouldThrowException() {
        notice.setDateTime(null);
        assertThrows(ServiceException.class,
            () -> {
                noticeService.save(notice);
            }
        );
    }

    @Test
    void deleteNoticeShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                noticeService.delete(21L);
            }
        );
    }

    @Test
    void deleteNoticeShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                noticeService.delete(3L);
            }
        );
    }

    @Test
    void deleteAllNoticeShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                noticeService.deleteAll(List.of(2L, 3L));
            }
        );
    }

    @Test
    void deleteAllNoticeShouldThrowException() {
        var list = List.of(21L, 4L);
        assertThrows(ServiceException.class,
            () -> {
                noticeService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableNoticeShouldReturnPage0() {
        var result = noticeService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnNotices() {
        var result = noticeService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByShouldReturnNotices() {
        var result = noticeService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2L, result.size());
    }

    @Test
    void findAllByShouldReturnSomeNotices() {
        var result = noticeService.findAllById(List.of(3L, 21L));
        assertNotNull(result);
        assertEquals(1L, result.size());
    }

    @Test
    void filterShouldReturnAllNotices() {
        var filter = new GenericFilterDTO();
        var result = noticeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3L, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeNotice() {
        var filter = new GenericFilterDTO();
        filter.setQuery("2");
        filter.setQueryFields(List.of("caravan.id"));
        var result = noticeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnNotice() {
        var result = noticeService.findOne(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOneShouldReturnNullNotice() {
        var result = noticeService.findOne(21L);
        assertNull(result);
    }
}
