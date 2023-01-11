package celtab.swge.repository;

import celtab.swge.model.URL;

import java.util.Optional;

public interface URLRepository extends GenericRepository<URL, Long> {

    Optional<URL> findByUrlFragment(String url);

}
