package celtab.swge.service;

import celtab.swge.model.RegistrationType;
import celtab.swge.repository.RegistrationTypeRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class RegistrationTypeService extends GenericService<RegistrationType, Long> {

    public RegistrationTypeService(RegistrationTypeRepository registrationTypeRepository) {
        super(registrationTypeRepository, "registration type(s)", new GenericSpecification<>(RegistrationType.class));
    }

}
