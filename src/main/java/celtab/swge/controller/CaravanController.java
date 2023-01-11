package celtab.swge.controller;

import celtab.swge.dto.CaravanRequestDTO;
import celtab.swge.dto.CaravanResponseDTO;
import celtab.swge.dto.TutoredUserRequestDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Caravan;
import celtab.swge.model.enums.CaravanType;
import celtab.swge.model.enums.ExclusionStatus;
import celtab.swge.model.enums.RequestType;
import celtab.swge.model.user.DeleteRequest;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/caravans")
public class CaravanController extends GenericController<Caravan, Long, CaravanRequestDTO, CaravanResponseDTO> {

    private static final String NOT_FOUND_STR = " Not Found!";

    private static final String NOT_POSSIBLE_ENROLLMENT = "It was not possible to finish the enrollment!";

    private static final String ENROLLMENT_NOT_MATCH_MSG = "This enrollment does not match the caravan type";

    private final CaravanService caravanService;

    private final UserService userService;

    private final TutoredUserService tutoredUserService;

    private final EditionService editionService;

    private final ExclusionService exclusionService;

    private final DeleteRequestService deleteRequestService;

    public CaravanController(
        CaravanService caravanService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper,
        UserService userService,
        TutoredUserService tutoredUserService,
        EditionService editionService, ExclusionService exclusionService, DeleteRequestService deleteRequestService) {
        super(caravanService, modelMapper, objectMapper);
        this.caravanService = caravanService;
        this.userService = userService;
        this.tutoredUserService = tutoredUserService;
        this.editionService = editionService;
        this.exclusionService = exclusionService;
        this.deleteRequestService = deleteRequestService;
    }

    private void verifyCaravanCoordinator(CaravanRequestDTO caravan) throws ControllerException {
        var caravanUser = editionService.isCoordinator(
            caravan.getEdition().getId(),
            caravan.getCoordinator().getId()
        );

        if (!caravanUser) {
            throw new ControllerException(BAD_REQUEST, "The user is not a caravan coordinator");
        }
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public CaravanResponseDTO create(@RequestBody CaravanRequestDTO caravan) {
        try {
            verifyCaravanCoordinator(caravan);
            return super.create(caravan);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private boolean verifyIsPossibleCaravanTypeChange(CaravanRequestDTO caravan) {
        var oldCaravan = caravanService.findOne(caravan.getId());
        return !(caravan.getType() != oldCaravan.getType() && (oldCaravan.getCaravanEnrollments().isEmpty() || oldCaravan.getCaravanTutoredEnrollments().isEmpty()));
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public CaravanResponseDTO update(@RequestBody CaravanRequestDTO caravan) {
        try {
            verifyCaravanCoordinator(caravan);

            if (verifyIsPossibleCaravanTypeChange(caravan))
                return super.update(caravan);

            throw new ControllerException(BAD_REQUEST, "Cannot change the type of a caravan with enrollments");
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping
    public Page<CaravanResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/edition/{editionId}")
    public Page<CaravanResponseDTO> findAll(@PathVariable Long editionId, Pageable pageable) {
        return mapTo(caravanService.findAllByEdition(editionId, pageable), CaravanResponseDTO.class);
    }

    @Override
    @GetMapping("/list")
    public List<CaravanResponseDTO> findAll() {
        return super.findAll();
    }

    @GetMapping("/edition/{editionId}/enrollments/{userId}")
    public List<CaravanResponseDTO> findAll(@PathVariable Long editionId, @PathVariable Long userId) {
        return mapTo(caravanService.findAllByEditionAndUserEnrollment(editionId, userId), CaravanResponseDTO.class);
    }

    @Override
    @GetMapping("/filter")
    public Page<CaravanResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public CaravanResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    private Caravan getCaravanWithType(Long caravanId, CaravanType caravanType) throws ControllerException {
        var caravan = service.findOne(caravanId);
        if (caravan == null) {
            throw new ControllerException(NOT_FOUND, service.getCustomModelNameMessage() + NOT_FOUND_STR);
        }
        if (caravan.getType() != caravanType) {
            throw new ControllerException(BAD_REQUEST, ENROLLMENT_NOT_MATCH_MSG);
        }
        return caravan;
    }

    private Caravan getGeneralCaravan(Long caravanId) throws ControllerException {
        return getCaravanWithType(caravanId, CaravanType.GENERAL);
    }

    private Caravan getTutoredCaravan(Long caravanId) throws ControllerException {
        return getCaravanWithType(caravanId, CaravanType.TUTORED);
    }

    @PostMapping("/enroll/{caravanId}/{userId}")
    @PreAuthorize("@webSecurity.isOwnCompletedUser(authentication, #userId)")
    @Transactional
    public void caravanEnrollment(@PathVariable Long caravanId, @PathVariable Long userId) {
        var caravan = getGeneralCaravan(caravanId);
        var user = userService.findOne(userId);
        if (user == null) {
            throw new ControllerException(NOT_FOUND, "User Not Found!");
        }
        try {
            caravanService.caravanEnrollment(caravan, user, false);
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, NOT_POSSIBLE_ENROLLMENT);
        }
    }

    @PostMapping("/enroll/list/{caravanId}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #caravanId)")
    @Transactional
    public void caravanEnrollment(@PathVariable Long caravanId, @RequestBody List<Long> userIds) {
        var caravan = getGeneralCaravan(caravanId);
        var users = userService.findAllById(userIds);
        if (users == null || users.size() != userIds.size()) {
            throw new ControllerException(NOT_FOUND, "User Not Found!");
        }
        try {
            caravanService.caravanEnrollment(caravan, users);
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, NOT_POSSIBLE_ENROLLMENT);
        }
    }

    @PostMapping("/enroll/tutored/list/{caravanId}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #caravanId)")
    @Transactional
    public void caravanEnrollmentTutoredList(@PathVariable Long caravanId, @RequestBody List<Long> tutoredUserIds) {
        var caravan = getTutoredCaravan(caravanId);
        var users = tutoredUserService.findAllById(tutoredUserIds);
        if (users == null || users.size() != tutoredUserIds.size()) {
            throw new ControllerException(NOT_FOUND, "Tutored Users Not Found!");
        }
        try {
            caravanService.caravanEnrollmentTutored(caravan, users);
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, NOT_POSSIBLE_ENROLLMENT);
        }
    }

    @PostMapping("/enroll/tutored/{caravanId}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #caravanId)")
    @Transactional
    public void caravanEnrollmentTutored(@PathVariable Long caravanId, @RequestBody TutoredUserRequestDTO tutoredUser) {
        var caravan = getTutoredCaravan(caravanId);
        TutoredUser realUser;
        if (tutoredUser.getId() != null) {
            var user = tutoredUserService.findOne(tutoredUser.getId());
            if (user == null) {
                throw new ControllerException(NOT_FOUND, "Tutored User Not Found!");
            }
            realUser = user;
        } else {
            realUser = mapTo(tutoredUser, TutoredUser.class);
            try {
                var savedTutored = tutoredUserService.save(realUser);
                var deleteRequest = new DeleteRequest();
                deleteRequest.setRequestDate(Date.from(Instant.now()));
                deleteRequest.setRequestType(RequestType.TUTORED);
                var savedDeleteRequest = deleteRequestService.save(deleteRequest);
                var exclusion = new Exclusion();
                exclusion.setTutoredUser(savedTutored);
                exclusion.setDeleteRequest(savedDeleteRequest);
                exclusion.setRegistryDate(Date.from(Instant.now()));
                exclusion.setStatus(ExclusionStatus.NOT_ANALYZED);
                exclusionService.sendUserExclusionCreatedMail(null, Boolean.TRUE, deleteRequest.getRequestType());
                exclusionService.save(exclusion);
            } catch (Exception e) {
                throw new ControllerException(CONFLICT, "It was not possible to create the tutored user!");
            }
        }
        try {
            caravanService.caravanEnrollment(caravan, realUser);
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, NOT_POSSIBLE_ENROLLMENT);
        }
    }

    @GetMapping("/unique/edition/name")
    public Boolean verifyUniqueName(@RequestParam String name, @RequestParam Long editionId) {
        return caravanService.findByNameAndEdition(name, editionId) != null;
    }

}

