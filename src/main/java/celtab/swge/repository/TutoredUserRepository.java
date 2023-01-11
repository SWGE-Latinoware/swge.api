package celtab.swge.repository;

import celtab.swge.model.user.TutoredUser;

import java.util.Optional;

public interface TutoredUserRepository extends GenericRepository<TutoredUser, Long> {

    Optional<TutoredUser> findByNameEquals(String name);

    Optional<TutoredUser> findByTagNameEquals(String tagName);

    Optional<TutoredUser> findByIdNumberEquals(String tagName);

}
