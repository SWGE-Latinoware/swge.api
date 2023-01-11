package celtab.swge.service;

import celtab.swge.model.Theme;
import celtab.swge.repository.ThemeRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class ThemeService extends GenericService<Theme, Long> {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        super(themeRepository, "theme(s)", new GenericSpecification<>(Theme.class));
        this.themeRepository = themeRepository;
    }

    public Theme findByName(String name) {
        return themeRepository.findByNameEquals(name).orElse(null);
    }

}
