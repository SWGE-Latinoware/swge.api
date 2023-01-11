package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.Activity;
import celtab.swge.model.BasicModel;
import celtab.swge.repository.ActivityRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ActivityService extends GenericService<Activity, Long> {

    private final ActivityRepository activityRepository;

    private final TrackService trackService;

    private final IndividualRegistrationScheduleService individualRegistrationScheduleService;

    public ActivityService(
        ActivityRepository activityRepository,
        TrackService trackService,
        IndividualRegistrationScheduleService individualRegistrationScheduleService
    ) {
        super(activityRepository, "activity(ies)", new GenericSpecification<>(Activity.class));
        this.activityRepository = activityRepository;
        this.trackService = trackService;
        this.individualRegistrationScheduleService = individualRegistrationScheduleService;
    }

    @Override
    @Transactional
    public Activity save(Activity activity) {
        try {
            var trackId = activity.getTrack().getId();
            var track = trackService.findOne(trackId);
            activity.setEdition(track.getEdition());
            var schedulesIds = activity
                .getSchedule()
                .stream()
                .map(BasicModel::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            individualRegistrationScheduleService.deleteAllByActivityAndNotInScheduleList(activity.getId(), schedulesIds);
            return super.save(activity);
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the " + customModelNameMessage + "!");
        }
    }

    public List<Activity> findAllByEdition(Long editionId) {
        return activityRepository.findAllByTrackEditionId(editionId);
    }

    public List<Activity> findAllLecturesByEdition(Long editionId) {
        return findAllByEdition(editionId).stream().filter(Activity::isLecture).collect(Collectors.toList());
    }

    public Activity findByNameAndEdition(String name, Long editionId) {
        return activityRepository.findByNameEqualsAndEditionId(name, editionId).orElse(null);
    }
}
