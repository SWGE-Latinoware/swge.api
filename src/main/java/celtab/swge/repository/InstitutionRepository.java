package celtab.swge.repository;

import celtab.swge.model.Institution;

import java.util.Optional;

public interface InstitutionRepository extends GenericRepository<Institution, Long> {

    Optional<Institution> findByNameEquals(String name);

}
