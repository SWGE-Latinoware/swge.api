package celtab.swge.repository;

import celtab.swge.model.Certificate;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends GenericRepository<Certificate, Long> {

    Optional<Certificate> findByNameEqualsAndEditionId(String name, Long editionId);

    List<Certificate> findAllByEditionId(Long editionId);

}
