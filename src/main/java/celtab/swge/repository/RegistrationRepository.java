package celtab.swge.repository;

import celtab.swge.model.registration.Registration;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends GenericRepository<Registration, Long> {

    Optional<Registration> findByEditionIdAndUserId(Long editionId, Long userId);

    List<Registration> findAllByEditionId(Long editionId);
}
