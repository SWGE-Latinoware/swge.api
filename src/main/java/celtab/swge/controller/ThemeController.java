package celtab.swge.controller;

import celtab.swge.dto.ThemeRequestDTO;
import celtab.swge.dto.ThemeResponseDTO;
import celtab.swge.model.Theme;
import celtab.swge.service.ThemeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/themes")
public class ThemeController extends GenericController<Theme, Long, ThemeRequestDTO, ThemeResponseDTO> {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(themeService, modelMapper, objectMapper);
        this.themeService = themeService;
    }

    @Override
    @PostMapping
    @Transactional
    public ThemeResponseDTO create(@RequestBody ThemeRequestDTO theme) {
        return super.create(theme);
    }

    @Override
    @PutMapping
    @Transactional
    public ThemeResponseDTO update(@RequestBody ThemeRequestDTO theme) {
        return super.update(theme);
    }

    @Override
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping
    public Page<ThemeResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/list")
    public List<ThemeResponseDTO> findAll() {
        return super.findAll();
    }

    @Override
    @GetMapping("/filter")
    public Page<ThemeResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public ThemeResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/unique/name")
    public Boolean verifyUniqueName(@RequestParam String name) {
        return themeService.findByName(name) != null;
    }

}
