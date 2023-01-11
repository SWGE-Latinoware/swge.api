package celtab.swge.service;

import celtab.swge.model.registration.Registration;
import celtab.swge.repository.RegistrationRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService extends GenericService<Registration, Long> {

    private final RegistrationRepository registrationRepository;

    public RegistrationService(RegistrationRepository registrationRepository) {
        super(registrationRepository, "registration(s)", new GenericSpecification<>(Registration.class));
        this.registrationRepository = registrationRepository;
    }

    public Registration findOneByEditionAndUser(Long editionId, Long userId) {
        return registrationRepository.findByEditionIdAndUserId(editionId, userId).orElse(null);
    }

    public List<Registration> findAllByEditionId(Long editionId) {
        return registrationRepository.findAllByEditionId(editionId);
    }
}
