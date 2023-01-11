package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.Theme;
import celtab.swge.model.enums.ThemeType;
import celtab.swge.util.GenericTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {
    "classpath:db.scripts/theme_data.sql",
    "classpath:db.scripts/users_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ThemeServiceTest extends GenericTestService {

    private Theme theme;

    @Autowired
    private ThemeService themeService;

    @BeforeEach
    private void init() {
        theme = new Theme();
        theme.setName("paleta 2021");
        theme.setType(ThemeType.DARK);
        theme.setColorPalette(Map.of("primary", "#4caf50", "secondary", "#292d31", "error", "#f44336"));
    }

    @Test
    void saveNewThemeShouldReturnTheme() {
        var result = themeService.save(theme);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void saveUpdateThemeShouldReturnTheme() {
        theme.setId(1L);
        var result = themeService.save(theme);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void saveNewThemeShouldThrowException() {
        theme.setName(null);
        assertThrows(ServiceException.class,
            () -> {
                themeService.save(theme);
            }
        );
    }

    @Test
    void deleteThemeShouldThrowException() {
        assertThrows(ServiceException.class,
            () -> {
                themeService.delete(21L);
            }
        );
    }

    @Test
    void deleteThemeShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                themeService.delete(2L);
            }
        );
    }

    @Test
    void deleteAllThemesShouldNotThrowException() {
        assertDoesNotThrow(
            () -> {
                themeService.deleteAll(List.of(1L, 2L));
            }
        );
    }

    @Test
    void deleteAllThemesShouldThrowException() {
        var list = List.of(21L, 4L, 2L);
        assertThrows(ServiceException.class,
            () -> {
                themeService.deleteAll(list);
            }
        );
    }

    @Test
    void findAllPageableThemeShouldReturnPage0() {
        var result = themeService.findAll(Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findAllListShouldReturnThemes() {
        var result = themeService.findAll();
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void findAllByIdShouldReturnThemes() {
        var result = themeService.findAllById(List.of(1L, 2L));
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllByIdShouldReturnSomeThemes() {
        var result = themeService.findAllById(List.of(2L, 21L));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterShouldReturnAllThemes() {
        var filter = new GenericFilterDTO();
        var result = themeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void filterShouldReturnSomeThemes() {
        var filter = new GenericFilterDTO();
        filter.setQuery("Paleta 1");
        filter.setQueryFields(List.of("name"));
        var result = themeService.filter(filter, Pageable.ofSize(10));
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void findOneShouldReturnTheme() {
        var result = themeService.findOne(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findOneShouldReturnNullTheme() {
        var result = themeService.findOne(21L);
        assertNull(result);
    }

    @Test
    void findByNameShouldReturnTheme() {
        var result = themeService.findByName("paleta 2");
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findByNameShouldReturnNull() {
        var result = themeService.findByName("paleta 42");
        assertNull(result);
    }

}
