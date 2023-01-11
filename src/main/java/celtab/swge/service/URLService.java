package celtab.swge.service;

import celtab.swge.model.URL;
import celtab.swge.repository.URLRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class URLService extends GenericService<URL, Long> {

    private final URLRepository urlRepository;

    public URLService(
        URLRepository urlRepository
    ) {
        super(urlRepository, "URL(s)", new GenericSpecification<>(URL.class));
        this.urlRepository = urlRepository;
    }

    public URL findByURL(String url) {
        return urlRepository.findByUrlFragment(url).orElse(null);
    }

}
