package celtab.swge.controller;

import celtab.swge.dto.RegistrationResponseDTO;
import celtab.swge.dto.TutoredRegistrationRequestDTO;
import celtab.swge.dto.TutoredRegistrationResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.service.TutoredRegistrationService;
import celtab.swge.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/tutored-registrations")
public class TutoredRegistrationController extends GenericController<TutoredRegistration, Long, TutoredRegistrationRequestDTO, TutoredRegistrationResponseDTO> implements DateTimeUtils {

    private final TutoredRegistrationService tutoredRegistrationService;

    public TutoredRegistrationController(
        TutoredRegistrationService tutoredRegistrationService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper
    ) {
        super(tutoredRegistrationService, modelMapper, objectMapper);
        this.tutoredRegistrationService = tutoredRegistrationService;
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
    @AdministratorFilter
    public Page<TutoredRegistrationResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @GetMapping("/edition/{editionId}/registration/{userId}")
    public RegistrationResponseDTO findOne(
        @PathVariable Long editionId,
        @PathVariable Long userId
    ) {
        var registration = tutoredRegistrationService.findOneByEditionAndTutoredUser(editionId, userId);
        if (registration == null) {
            throw new ControllerException(NOT_FOUND, "It was not possible to fetch the registration!");
        }
        return mapTo(registration, RegistrationResponseDTO.class);
    }
}
