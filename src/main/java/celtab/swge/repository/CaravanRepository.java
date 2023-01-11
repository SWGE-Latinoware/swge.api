package celtab.swge.repository;

import celtab.swge.model.Caravan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CaravanRepository extends GenericRepository<Caravan, Long> {

    Page<Caravan> findAllByEditionId(Long editionId, Pageable pageable);

    List<Caravan> findAllByEditionId(Long editionId);

    List<Caravan> findAllByEditionIdAndCoordinatorId(Long editionId, Long coordinatorId);

    Optional<Caravan> findByNameEqualsAndEditionId(String name, Long editionId);
    
}
