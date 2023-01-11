package celtab.swge.service;

import celtab.swge.model.user.User;
import celtab.swge.repository.UserRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;


@Service
public class UserService extends GenericService<User, Long> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        super(userRepository, "user(s)", new GenericSpecification<>(User.class));
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailEquals(email).orElse(null);
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleIdEquals(googleId).orElse(null);
    }

    public User findByGithubId(String githubId) {
        return userRepository.findByGithubIdEquals(githubId).orElse(null);
    }

    public User findByName(String name) {
        return userRepository.findByNameEquals(name).orElse(null);
    }

    public User findByTagName(String tagName) {
        return userRepository.findByTagNameEquals(tagName).orElse(null);
    }

}
