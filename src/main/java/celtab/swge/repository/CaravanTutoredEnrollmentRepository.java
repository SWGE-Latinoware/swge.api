package celtab.swge.repository;

import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaravanTutoredEnrollmentRepository extends GenericRepository<CaravanTutoredEnrollment, Long> {

    Page<CaravanTutoredEnrollment> findAllByCaravanId(Long caravanId, Pageable pageable);

}
