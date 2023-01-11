package celtab.swge.repository;

import celtab.swge.model.Theme;

import java.util.Optional;

public interface ThemeRepository extends GenericRepository<Theme, Long> {

    Optional<Theme> findByNameEquals(String name);

}
