package celtab.swge.repository;

import celtab.swge.model.Track;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends GenericRepository<Track, Long> {

    Optional<Track> findByNameEqualsAndEditionId(String name, Long editionId);

    List<Track> findAllByEditionId(Long editionId);

}
