package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.URL;
import celtab.swge.model.enums.URLType;
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
    "classpath:db.scripts/url_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class URLServiceTest extends GenericTestService {

    private URL url;

    @Autowired
    private URLService urlService;

    @BeforeEach
    private void init() {
        var user = new User();
        user.setId(3L);
        url = new URL();
        url.setEmail("natan@gmail.teste.com");
        url.setUrlFragment("399b940a-3266-469c-8975-5a0b90ac9ea3");
        url.setType(URLType.EMAIL_CONFIRMATION);
        url.setUser(user);
    }

    @Test
    void saveNewUrlShouldReturnUrl() {
        var result = urlService.save(url);
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void saveUpdateUrlShouldReturnUrl() {
        url.setId(1L);
        var result = urlService.save(url);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewUrlShouldThrowException() {
        url.setUrlFragment(null);
        assertThrows(ServiceException.class,
            () -> {
                urlService.save(url);
            }
        );
    }

    @Test
    void deleteUrlShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                urlService.delete(21L);
            }
        );
    }

    @Test
    void deleteUrlShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                urlService.delete(1L);
            }
        );
    }

    @Test
    void deleteAllUrlShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                urlService.deleteAll(List.of(1L, 2L));
            }
        );
    }

    @Test
    void deleteAllUrlShouldThrowException() {
        var list = List.of(21L, 42L);
        assertThrows(ServiceException.class,
            () -> {
                urlService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableUrlShouldReturnPage0() {
        var result = urlService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnUrls() {
        var result = urlService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnUrls() {
        var result = urlService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeUrls() {
        var result = urlService.findAllById(List.of(1L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllUrls() {
        var filter = new GenericFilterDTO();
        var result = urlService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeUrls() {
        var filter = new GenericFilterDTO();
        filter.setQuery("rafael@gmail.teste.com");
        filter.setQueryFields(List.of("email"));
        var result = urlService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnUrl() {
        var result = urlService.findOne(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findOneShouldReturnNullUrl() {
        var result = urlService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findByUrlShouldReturnUrl() {
        var result = urlService.findByURL("399b940a-3266-469c-8975-5a0b90ac9ea6");
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByUrlShouldReturnNull() {
        var result = urlService.findByURL("399b940a-3266-469c-8975-5a0b90ac9");
        assertNull(result);
    }
}
