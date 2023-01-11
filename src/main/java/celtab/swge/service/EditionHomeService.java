package celtab.swge.service;

import celtab.swge.model.EditionHome;
import celtab.swge.repository.EditionHomeRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditionHomeService extends GenericService<EditionHome, Long>{

    private final EditionHomeRepository editionHomeRepository;

    public EditionHomeService(EditionHomeRepository editionHomeRepository ) {
        super(editionHomeRepository,
            "editionHome", new GenericSpecification<>(EditionHome.class)
        );
        this.editionHomeRepository = editionHomeRepository;
    }

    public EditionHome findByEditionIdAndLanguage(Long editionId, String language){
        return editionHomeRepository.findByEditionIdAndLanguage(editionId, language);
    }

    public List<EditionHome> findAllByEditionId(Long editionId){
        return editionHomeRepository.findAllByEditionId(editionId);
    }
}
