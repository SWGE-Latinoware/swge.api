package celtab.swge.controller;

import celtab.swge.dto.CaravanEnrollmentRequestDTO;
import celtab.swge.dto.CaravanEnrollmentResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.service.CaravanEnrollmentService;
import celtab.swge.service.EditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/caravan-enrollments")
public class CaravanEnrollmentController extends GenericController<CaravanEnrollment, Long, CaravanEnrollmentRequestDTO, CaravanEnrollmentResponseDTO> {

    private final CaravanEnrollmentService caravanEnrollmentService;

    private final EditionService editionService;

    public CaravanEnrollmentController(
        CaravanEnrollmentService caravanEnrollmentService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper,
        EditionService editionService) {
        super(caravanEnrollmentService, modelMapper, objectMapper);
        this.caravanEnrollmentService = caravanEnrollmentService;
        this.editionService = editionService;
    }

    @Override
    @GetMapping("/list/ids")
    public List<CaravanEnrollmentResponseDTO> findAllById(@RequestParam(value = "ids") List<Long> ids) {
        return super.findAllById(ids);
    }

    @Override
    @PutMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinatorOrOwnCompletedUser(authentication, #caravanEnrollment.caravan.id, #caravanEnrollment.user.id)")
    @Transactional
    public CaravanEnrollmentResponseDTO update(@RequestBody CaravanEnrollmentRequestDTO caravanEnrollment) {
        return super.update(caravanEnrollment);
    }

    @Override
    @DeleteMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinatorEnrollment(authentication, #ids)")
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @GetMapping("/caravan/{caravanId}")
    public Page<CaravanEnrollmentResponseDTO> findAllByCaravan(@PathVariable Long caravanId, Pageable pageable) {
        return mapTo(caravanEnrollmentService.findAllByCaravan(caravanId, pageable), CaravanEnrollmentResponseDTO.class);
    }


    @GetMapping("/verify-enrollment/edition/{editionId}/user/{userId}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUser(authentication, #userId)")
    public boolean verifyCaravanNotPay(@PathVariable Long editionId, @PathVariable Long userId) {
        var edition = editionService.findOne(editionId);

        if (edition == null) {
            throw new ControllerException(HttpStatus.NOT_FOUND, "Edition Not Found!");
        }

        return !edition.hasUserWithCustomCaravanEnrollment(userId, true, true, false);
    }
}
