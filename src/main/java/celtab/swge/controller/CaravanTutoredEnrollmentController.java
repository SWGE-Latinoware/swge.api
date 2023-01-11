package celtab.swge.controller;

import celtab.swge.dto.CaravanTutoredEnrollmentRequestDTO;
import celtab.swge.dto.CaravanTutoredEnrollmentResponseDTO;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.service.CaravanTutoredEnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/caravan-tutored-enrollments")
public class CaravanTutoredEnrollmentController extends GenericController<CaravanTutoredEnrollment, Long, CaravanTutoredEnrollmentRequestDTO, CaravanTutoredEnrollmentResponseDTO> {

    private final CaravanTutoredEnrollmentService caravanTutoredEnrollmentService;

    public CaravanTutoredEnrollmentController(CaravanTutoredEnrollmentService caravanTutoredEnrollmentService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(caravanTutoredEnrollmentService, modelMapper, objectMapper);
        this.caravanTutoredEnrollmentService = caravanTutoredEnrollmentService;
    }

    @Override
    @GetMapping("/list/ids")
    public List<CaravanTutoredEnrollmentResponseDTO> findAllById(@RequestParam(value = "ids") List<Long> ids) {
        return super.findAllById(ids);
    }

    @Override
    @PutMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #caravanEnrollment.caravan.id)")
    @Transactional
    public CaravanTutoredEnrollmentResponseDTO update(@RequestBody CaravanTutoredEnrollmentRequestDTO caravanEnrollment) {
        return super.update(caravanEnrollment);
    }

    @Override
    @DeleteMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinatorTutoredEnrollment(authentication, #ids)")
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @GetMapping("/caravan/{caravanId}")
    public Page<CaravanTutoredEnrollmentResponseDTO> findAllByCaravan(@PathVariable Long caravanId, Pageable pageable) {
        return mapTo(caravanTutoredEnrollmentService.findAllByCaravan(caravanId, pageable), CaravanTutoredEnrollmentResponseDTO.class);
    }

}
