package celtab.swge.repository;

import celtab.swge.model.enrollment.CaravanEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaravanEnrollmentRepository extends GenericRepository<CaravanEnrollment, Long> {

    Page<CaravanEnrollment> findAllByCaravanId(Long caravanId, Pageable pageable);

    List<CaravanEnrollment> findAllByCaravanEditionIdAndUserId(Long editionId, Long userId);
}
