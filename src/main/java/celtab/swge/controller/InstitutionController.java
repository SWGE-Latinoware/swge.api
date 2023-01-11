package celtab.swge.controller;

import celtab.swge.dto.InstitutionRequestDTO;
import celtab.swge.dto.InstitutionResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Institution;
import celtab.swge.service.InstitutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/institutions")
public class InstitutionController extends GenericController<Institution, Long, InstitutionRequestDTO, InstitutionResponseDTO> {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(institutionService, modelMapper, objectMapper);
        this.institutionService = institutionService;
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public InstitutionResponseDTO create(@RequestBody InstitutionRequestDTO institution) {
        try {
            institution.getSpaces().forEach(space -> space.setInstitution(institution));
            return super.create(institution);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public InstitutionResponseDTO update(@RequestBody InstitutionRequestDTO institution) {
        try {
            institution.getSpaces().forEach(space -> space.setInstitution(institution));
            return super.update(institution);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
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
    @GetMapping
    public Page<InstitutionResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/list")
    public List<InstitutionResponseDTO> findAll() {
        return super.findAll();
    }

    @Override
    @GetMapping("/filter")
    public Page<InstitutionResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public InstitutionResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/unique/name")
    public Boolean verifyUniqueName(@RequestParam String name) {
        return institutionService.findByName(name) != null;
    }
}
