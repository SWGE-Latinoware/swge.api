package celtab.swge.repository;

import celtab.swge.model.registration.TutoredRegistration;

import java.util.Optional;

public interface TutoredRegistrationRepository extends GenericRepository<TutoredRegistration, Long> {

    Optional<TutoredRegistration> findByEditionIdAndTutoredUserId(Long editionId, Long userId);
}
