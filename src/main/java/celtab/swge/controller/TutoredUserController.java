package celtab.swge.controller;

import celtab.swge.dto.TutoredUserRequestDTO;
import celtab.swge.dto.TutoredUserResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.service.TutoredUserService;
import celtab.swge.util.UUIDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutored-users")
public class TutoredUserController extends GenericController<TutoredUser, Long, TutoredUserRequestDTO, TutoredUserResponseDTO> implements UUIDUtils {

    private final TutoredUserService tutoredUserService;

    public TutoredUserController(TutoredUserService tutoredUserService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(tutoredUserService, modelMapper, objectMapper);
        this.tutoredUserService = tutoredUserService;
    }

    @Override
    @PutMapping
    @Transactional
    public TutoredUserResponseDTO update(@RequestBody TutoredUserRequestDTO tutoredUser) {
        return super.update(tutoredUser);
    }

    @Override
    @GetMapping("/{id}")
    public TutoredUserResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @Override
    @GetMapping("/filter")
    public Page<TutoredUserResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @GetMapping("/unique/name")
    public Boolean verifyUniqueName(@RequestParam String name) {
        return tutoredUserService.findByName(name) != null;
    }

    @GetMapping("/unique/tag-name")
    public Boolean verifyUniqueTagName(@RequestParam String tagName) {
        return tutoredUserService.findByTagName(tagName) != null;
    }

    @GetMapping("/unique/id-number")
    public Boolean verifyUniqueIdNumber(@RequestParam String idNumber) {
        return tutoredUserService.findByIdNumber(idNumber) != null;
    }

    @Override
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        throw new ControllerException(HttpStatus.INTERNAL_SERVER_ERROR, "Method Not Allowed");
    }

    @Override
    @DeleteMapping()
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        throw new ControllerException(HttpStatus.INTERNAL_SERVER_ERROR, "Method Not Allowed");
    }
}
