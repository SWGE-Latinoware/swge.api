package celtab.swge.service;

import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.repository.TutoredRegistrationRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class TutoredRegistrationService extends GenericService<TutoredRegistration, Long> {

    private final TutoredRegistrationRepository tutoredRegistrationRepository;

    public TutoredRegistrationService(TutoredRegistrationRepository tutoredRegistrationRepository) {
        super(tutoredRegistrationRepository, "tutored registration(s)", new GenericSpecification<>(TutoredRegistration.class));
        this.tutoredRegistrationRepository = tutoredRegistrationRepository;
    }

    public TutoredRegistration findOneByEditionAndTutoredUser(Long editionId, Long userId) {
        return tutoredRegistrationRepository.findByEditionIdAndTutoredUserId(editionId, userId).orElse(null);
    }
}
