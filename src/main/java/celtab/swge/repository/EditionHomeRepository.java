package celtab.swge.repository;

import celtab.swge.model.EditionHome;

import java.util.List;

public interface EditionHomeRepository extends GenericRepository<EditionHome, Long> {
    EditionHome findByEditionIdAndLanguage(Long editionId, String language);

    List<EditionHome> findAllByEditionId(Long editionId);
}
