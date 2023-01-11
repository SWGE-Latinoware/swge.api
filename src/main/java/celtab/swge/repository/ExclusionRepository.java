package celtab.swge.repository;

import celtab.swge.model.user.Exclusion;

import java.util.List;

public interface ExclusionRepository extends GenericRepository<Exclusion, Long> {

    List<Exclusion> findAllByUserId(Long id);
}
