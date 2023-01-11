package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.model.registration.individual_registration.TutoredIndividualRegistration;
import celtab.swge.repository.CaravanTutoredEnrollmentRepository;
import celtab.swge.specification.GenericSpecification;
import celtab.swge.util.EnvironmentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CaravanTutoredEnrollmentService extends GenericService<CaravanTutoredEnrollment, Long> {

    private final CaravanTutoredEnrollmentRepository caravanTutoredEnrollmentRepository;

    private final TutoredRegistrationService tutoredRegistrationService;

    private final ActivityService activityService;

    private final EntityManager entityManager;

    private final EnvironmentInfo environmentInfo;

    public CaravanTutoredEnrollmentService(
        CaravanTutoredEnrollmentRepository caravanTutoredEnrollmentRepository,
        TutoredRegistrationService tutoredRegistrationService,
        ActivityService activityService,
        EntityManager entityManager,
        EnvironmentInfo environmentInfo) {
        super(caravanTutoredEnrollmentRepository, "caravan tutored enrollment(s)", new GenericSpecification<>(CaravanTutoredEnrollment.class));
        this.caravanTutoredEnrollmentRepository = caravanTutoredEnrollmentRepository;
        this.tutoredRegistrationService = tutoredRegistrationService;
        this.activityService = activityService;
        this.entityManager = entityManager;
        this.environmentInfo = environmentInfo;
    }

    @Override
    @Transactional
    public CaravanTutoredEnrollment save(CaravanTutoredEnrollment caravanTutoredEnrollment) {
        try {
            var enrollment = super.save(caravanTutoredEnrollment);
            if (environmentInfo.isTest()) {
                entityManager.refresh(enrollment);
            }
            var registration = tutoredRegistrationService.findOneByEditionAndTutoredUser(
                enrollment.getCaravan().getEdition().getId(),
                enrollment.getTutoredUser().getId()
            );
            if (Boolean.TRUE.equals(enrollment.getAccepted()) && Boolean.TRUE.equals(enrollment.getPayed())) {
                if (registration == null) {
                    var newRegistration = new TutoredRegistration();
                    newRegistration.setTutoredUser(enrollment.getTutoredUser());
                    newRegistration.setEdition(enrollment.getCaravan().getEdition());
                    newRegistration.setPayed(true);
                    newRegistration.setOriginalPrice(enrollment.getCaravan().getPrice());
                    newRegistration.setFinalPrice(enrollment.getCaravan().getPrice());
                    var individualRegistrations = activityService
                        .findAllLecturesByEdition(enrollment.getCaravan().getEdition().getId())
                        .stream()
                        .map(activity -> new TutoredIndividualRegistration(activity, newRegistration))
                        .collect(Collectors.toCollection(ArrayList::new));
                    newRegistration.setIndividualRegistrations(individualRegistrations);
                    tutoredRegistrationService.save(newRegistration);
                }
            } else {
                if (registration != null) {
                    tutoredRegistrationService.delete(registration.getId());
                }
            }
            return enrollment;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the " + customModelNameMessage + "!");
        }
    }

    public Page<CaravanTutoredEnrollment> findAllByCaravan(Long caravanId, Pageable pageable) {
        return caravanTutoredEnrollmentRepository.findAllByCaravanId(caravanId, pageable);
    }

}
