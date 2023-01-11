package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.Caravan;
import celtab.swge.model.enrollment.BasicEnrollment;
import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.model.user.User;
import celtab.swge.repository.CaravanRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaravanService extends GenericService<Caravan, Long> {

    private final CaravanRepository caravanRepository;

    private final CaravanEnrollmentService caravanEnrollmentService;

    private final CaravanTutoredEnrollmentService caravanTutoredEnrollmentService;

    public CaravanService(
        CaravanRepository caravanRepository,
        CaravanEnrollmentService caravanEnrollmentService,
        CaravanTutoredEnrollmentService caravanTutoredEnrollmentService) {
        super(caravanRepository, "caravan(s)", new GenericSpecification<>(Caravan.class));
        this.caravanRepository = caravanRepository;
        this.caravanEnrollmentService = caravanEnrollmentService;
        this.caravanTutoredEnrollmentService = caravanTutoredEnrollmentService;
    }

    public Page<Caravan> findAllByEdition(Long editionId, Pageable pageable) {
        return caravanRepository.findAllByEditionId(editionId, pageable);
    }

    public List<Caravan> findAllByEdition(Long editionId) {
        return caravanRepository.findAllByEditionId(editionId);
    }

    public List<Caravan> findAllByEditionAndUserEnrollment(Long editionId, Long userId) {
        return caravanEnrollmentService
            .findAllByEditionIdAndUserId(editionId, userId)
            .stream()
            .map(BasicEnrollment::getCaravan)
            .collect(Collectors.toList());
    }

    @Transactional
    public void caravanEnrollment(Caravan caravan, User user, boolean isCoordinatorInsertion) {
        var enrollment = caravan
            .getCaravanEnrollments()
            .stream()
            .filter(caravanEnrollment -> caravanEnrollment.getUser().getId().equals(user.getId()))
            .findAny()
            .orElse(null);
        if (caravan.getEdition().getCaravans().stream().anyMatch(caravanTemp ->
            caravanTemp.getCaravanEnrollments().stream().anyMatch(caravanEnrollment ->
                caravanEnrollment.getUser().getId().equals(user.getId()) && caravanEnrollment.getAccepted()
            )
        )) {
            throw new ServiceException("It was not possible finish the enrollment! User already enrolled and accepted in another caravan.");
        }
        if (caravan.getRemainingVacancies() == 0) {
            throw new ServiceException("It was not possible to finish the enrollment! No remaining vacancies.");
        }
        if (enrollment == null) {
            enrollment = new CaravanEnrollment();
            enrollment.setCaravan(caravan);
            if (isCoordinatorInsertion) {
                enrollment.setConfirmed(false);
                enrollment.setAccepted(true);
            } else {
                enrollment.setConfirmed(true);
                enrollment.setAccepted(false);
            }
            enrollment.setUser(user);
            enrollment.setPayed(false);
        } else {
            if (isCoordinatorInsertion) {
                enrollment.setAccepted(true);
            } else {
                enrollment.setConfirmed(true);
            }
        }
        try {
            caravanEnrollmentService.save(enrollment);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the caravan enrollment!");
        }
    }

    @Transactional
    public void caravanEnrollment(Caravan caravan, List<User> users) {
        users.forEach(user -> caravanEnrollment(caravan, user, true));
    }

    @Transactional
    public void caravanEnrollmentTutored(Caravan caravan, List<TutoredUser> users) {
        users.forEach(user -> caravanEnrollment(caravan, user));
    }

    @Transactional
    public void caravanEnrollment(Caravan caravan, TutoredUser user) {
        if (caravan
            .getCaravanTutoredEnrollments()
            .stream()
            .anyMatch(caravanEnrollment -> caravanEnrollment.getTutoredUser().getId().equals(user.getId()))) {
            throw new ServiceException("It was not possible finish the enrollment! User already enrolled.");
        }
        if (caravan.getEdition().getCaravans().stream().anyMatch(caravanTemp ->
            caravanTemp.getCaravanTutoredEnrollments().stream().anyMatch(caravanEnrollment ->
                caravanEnrollment.getTutoredUser().getId().equals(user.getId()) && caravanEnrollment.getAccepted()
            )
        )) {
            throw new ServiceException("It was not possible finish the enrollment! User already enrolled and accepted in another caravan.");
        }
        if (caravan.getRemainingVacancies() == 0) {
            throw new ServiceException("It was not possible to finish the enrollment! No remaining vacancies.");
        }
        var enrollment = new CaravanTutoredEnrollment();
        enrollment.setCaravan(caravan);
        enrollment.setAccepted(false);
        enrollment.setTutoredUser(user);
        enrollment.setPayed(false);
        try {
            caravanTutoredEnrollmentService.save(enrollment);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the caravan enrollment!");
        }
    }

    public Caravan findByNameAndEdition(String name, Long editionId) {
        return caravanRepository.findByNameEqualsAndEditionId(name, editionId).orElse(null);
    }

    public List<Caravan> findAllByEditionAndCoordinator(Long editionId, Long coordinatorId) {
        return caravanRepository.findAllByEditionIdAndCoordinatorId(editionId, coordinatorId);
    }

}
