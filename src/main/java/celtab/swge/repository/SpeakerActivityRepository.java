package celtab.swge.repository;

import celtab.swge.model.SpeakerActivity;

import java.util.List;

public interface SpeakerActivityRepository extends GenericRepository<SpeakerActivity, Long> {

    void deleteAllByActivityId(Long activityId);

    List<SpeakerActivity> findAllBySpeakerIdAndActivityEditionId(Long speakerId, Long editionId);
}
