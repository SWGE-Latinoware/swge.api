package celtab.swge.controller;

import celtab.swge.dto.TrackRequestDTO;
import celtab.swge.dto.TrackResponseDTO;
import celtab.swge.model.Track;
import celtab.swge.service.TrackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static celtab.swge.security.WebSecurity.AdministratorFilter;

@RestController
@RequestMapping("/tracks")
public class TrackController extends GenericController<Track, Long, TrackRequestDTO, TrackResponseDTO> {

    private final TrackService trackService;

    public TrackController(TrackService trackService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(trackService, modelMapper, objectMapper);
        this.trackService = trackService;
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public TrackResponseDTO create(@RequestBody TrackRequestDTO track) {
        return super.create(track);
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public TrackResponseDTO update(@RequestBody TrackRequestDTO track) {
        return super.update(track);
    }

    @Override
    @GetMapping("/{id}")
    public TrackResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping("/filter")
    public Page<TrackResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @GetMapping("/unique/edition/name")
    public Boolean verifyUniqueName(@RequestParam String name, @RequestParam Long editionId) {
        return trackService.findByNameAndEdition(name, editionId) != null;
    }

}
