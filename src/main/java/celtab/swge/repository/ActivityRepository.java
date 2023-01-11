package celtab.swge.repository;

import celtab.swge.model.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends GenericRepository<Activity, Long> {

    List<Activity> findAllByTrackEditionId(Long editionId);

    Optional<Activity> findByNameEqualsAndEditionId(String name, Long editionId);
}
