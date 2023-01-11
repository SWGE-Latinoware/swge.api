package celtab.swge.service;

import celtab.swge.model.Track;
import celtab.swge.repository.TrackRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService extends GenericService<Track, Long> {

    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        super(trackRepository, "track(s)", new GenericSpecification<>(Track.class));
        this.trackRepository = trackRepository;
    }

    public Track findByNameAndEdition(String name, Long editionId) {
        return trackRepository.findByNameEqualsAndEditionId(name, editionId).orElse(null);
    }

    public List<Track> findAllByEdition(Long editionId) {
        return trackRepository.findAllByEditionId(editionId);
    }

}
