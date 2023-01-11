package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Feedback;
import celtab.swge.model.File;
import celtab.swge.model.enums.FeedbackStatus;
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
    "classpath:db.scripts/feedback_data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FeedbackServiceTest extends GenericTestService {

    private Feedback feedback;

    @Autowired
    private FeedbackService feedbackService;

    @BeforeEach
    private void init() {
        var file = new File();
        file.setId(1L);
        var user = new User();
        user.setId(1L);
        feedback = new Feedback();
        feedback.setTitle("Report Bugs");
        feedback.setStatus(FeedbackStatus.OPEN);
        feedback.setFile(file);
        feedback.setUser(user);
    }

    @Test
    void saveNewfeedbackShouldReturnfeedback() {
        var result = feedbackService.save(feedback);
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }

    @Test
    void saveUpdatefeedbackShouldReturnfeedback() {
        feedback.setId(1L);
        var result = feedbackService.save(feedback);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewfeedbackShouldThrowException() {
        feedback.setTitle(null);
        assertThrows(ServiceException.class,
            () -> {
                feedbackService.save(feedback);
            }
        );
    }

    @Test
    void deleteFeedbackShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                feedbackService.delete(21L);
            }
        );
    }

    @Test
    void deleteFeedbackShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                feedbackService.delete(1L);
            }
        );
    }

    @Test
    void deleteAllFeedbackShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                feedbackService.deleteAll(List.of(1L, 3L));
            }
        );
    }

    @Test
    void deleteAllFeedbackShouldThrowException() {
        var list = List.of(21L, 24L);
        assertThrows(ServiceException.class,
            () -> {
                feedbackService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageablefeedbackShouldReturnPage0() {
        var result = feedbackService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnfeedbacks() {
        var result = feedbackService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findAllByIdShouldReturnfeedbacks() {
        var result = feedbackService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomefeedbacks() {
        var result = feedbackService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllfeedbacks() {
        var filter = new GenericFilterDTO();
        var result = feedbackService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomefeedback() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Bug");
        filter.setQueryFields(List.of("title"));
        var result = feedbackService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnfeedback() {
        var result = feedbackService.findOne(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findOneShouldReturnNullfeedback() {
        var result = feedbackService.findOne(21L);
        assertNull(result);
    }
}
