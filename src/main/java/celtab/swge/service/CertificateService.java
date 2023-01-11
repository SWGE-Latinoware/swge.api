package celtab.swge.service;

import celtab.swge.model.Certificate;
import celtab.swge.repository.CertificateRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService extends GenericService<Certificate, Long> {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        super(certificateRepository, "certificate(s)", new GenericSpecification<>(Certificate.class));
        this.certificateRepository = certificateRepository;
    }

    public Certificate findByNameAndEdition(String name, Long editionId) {
        return certificateRepository.findByNameEqualsAndEditionId(name, editionId).orElse(null);
    }

    public List<Certificate> findAllByEdition(Long editionId) {
        return certificateRepository.findAllByEditionId(editionId);
    }
}
