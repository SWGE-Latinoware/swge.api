package celtab.swge.repository;

import celtab.swge.model.user.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long> {

    Optional<User> findByEmailEquals(String email);

    Optional<User> findByNameEquals(String name);

    Optional<User> findByTagNameEquals(String tagName);

    Optional<User> findByGoogleIdEquals(String googleId);

    Optional<User> findByGithubIdEquals(String googleId);

}
