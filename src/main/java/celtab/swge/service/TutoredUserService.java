package celtab.swge.service;

import celtab.swge.model.user.TutoredUser;
import celtab.swge.repository.TutoredUserRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class TutoredUserService extends GenericService<TutoredUser, Long> {

    private final TutoredUserRepository tutoredUserRepository;

    public TutoredUserService(TutoredUserRepository tutoredUserRepository) {
        super(tutoredUserRepository, "tutored user(s)", new GenericSpecification<>(TutoredUser.class));
        this.tutoredUserRepository = tutoredUserRepository;
    }

    public TutoredUser findByName(String name) {
        return tutoredUserRepository.findByNameEquals(name).orElse(null);
    }

    public TutoredUser findByTagName(String tagName) {
        return tutoredUserRepository.findByTagNameEquals(tagName).orElse(null);
    }

    public TutoredUser findByIdNumber(String idNumber) {
        return tutoredUserRepository.findByIdNumberEquals(idNumber).orElse(null);
    }

}
