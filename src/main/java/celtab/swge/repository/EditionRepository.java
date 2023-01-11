package celtab.swge.repository;

import celtab.swge.model.Edition;

import java.util.Optional;

public interface EditionRepository extends GenericRepository<Edition, Long> {

    Optional<Edition> findByNameEquals(String name);

    Optional<Edition> findByShortNameEquals(String shortName);

    Optional<Edition> findByYear(Integer year);

}
