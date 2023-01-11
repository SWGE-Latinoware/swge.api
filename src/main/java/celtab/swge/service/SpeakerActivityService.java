package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.SpeakerActivity;
import celtab.swge.repository.SpeakerActivityRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerActivityService extends GenericService<SpeakerActivity, Long> {

    private final SpeakerActivityRepository speakerActivityRepository;

    public SpeakerActivityService(SpeakerActivityRepository speakerActivityRepository) {
        super(speakerActivityRepository, "speaker activity(iss)", new GenericSpecification<>(SpeakerActivity.class));
        this.speakerActivityRepository = speakerActivityRepository;
    }

    @Transactional
    public void deleteAllByActivity(Long activityId) {
        try {
            speakerActivityRepository.deleteAllByActivityId(activityId);
        } catch (RuntimeException e) {
            throw new ServiceException("It was not possible to delete the " + customModelNameMessage + "!");
        }
    }

    public List<SpeakerActivity> findAllByEditionAndSpeaker(Long editionId, Long speakerId) {
        return speakerActivityRepository.findAllBySpeakerIdAndActivityEditionId(speakerId, editionId);
    }
}
