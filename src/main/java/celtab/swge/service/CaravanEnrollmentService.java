package celtab.swge.service;

import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.repository.CaravanEnrollmentRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaravanEnrollmentService extends GenericService<CaravanEnrollment, Long> {

    private final CaravanEnrollmentRepository caravanEnrollmentRepository;

    public CaravanEnrollmentService(CaravanEnrollmentRepository caravanEnrollmentRepository) {
        super(caravanEnrollmentRepository, "caravan enrollment(s)", new GenericSpecification<>(CaravanEnrollment.class));
        this.caravanEnrollmentRepository = caravanEnrollmentRepository;
    }

    public Page<CaravanEnrollment> findAllByCaravan(Long caravanId, Pageable pageable) {
        return caravanEnrollmentRepository.findAllByCaravanId(caravanId, pageable);
    }

    public List<CaravanEnrollment> findAllByEditionIdAndUserId(Long editionId, Long userId) {
        return caravanEnrollmentRepository.findAllByCaravanEditionIdAndUserId(editionId, userId);
    }
}
