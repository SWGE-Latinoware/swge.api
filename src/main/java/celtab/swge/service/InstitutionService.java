package celtab.swge.service;

import celtab.swge.model.Institution;
import celtab.swge.repository.InstitutionRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService extends GenericService<Institution, Long> {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        super(institutionRepository, "institution(s)", new GenericSpecification<>(Institution.class));
        this.institutionRepository = institutionRepository;
    }

    public Institution findByName(String name) {
        return institutionRepository.findByNameEquals(name).orElse(null);
    }
}
