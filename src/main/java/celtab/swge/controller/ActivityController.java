package celtab.swge.controller;

import celtab.swge.dto.ActivityRequestDTO;
import celtab.swge.dto.ActivityResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Activity;
import celtab.swge.model.Track;
import celtab.swge.service.ActivityService;
import celtab.swge.service.EditionService;
import celtab.swge.service.TrackService;
import celtab.swge.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/activities")
public class ActivityController extends GenericController<Activity, Long, ActivityRequestDTO, ActivityResponseDTO> implements DateTimeUtils {

    private static final String TRACK_NOT_FOUND = "Track not Found!";

    private final EditionService editionService;

    private final TrackService trackService;

    private final ActivityService activityService;

    public ActivityController(
        ActivityService activityService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper,
        EditionService editionService,
        TrackService trackService) {
        super(activityService, modelMapper, objectMapper);
        this.editionService = editionService;
        this.activityService = activityService;
        this.trackService = trackService;
    }

    private void validateSchedule(ActivityRequestDTO activity, Track track) throws ControllerException {
        if (!activity.getSchedule().stream().allMatch(schedule -> isIntervalInInterval(
            schedule.getStartDateTime(),
            schedule.getEndDateTime(),
            track.getInitialDate(),
            Date.from(track.getFinalDate().toInstant().plusSeconds(1))
        ))) {
            throw new ControllerException(BAD_REQUEST, "Schedule out of the track interval");
        }
    }

    private void validatePlaceTimeConflict(ActivityRequestDTO activity, List<Activity> activities) throws ControllerException {
        activities.removeIf(activity1 -> Objects.equals(activity1.getId(), activity.getId()));
        if (activities
            .stream()
            .anyMatch(activity1 ->
                activity1
                    .getSchedule()
                    .stream()
                    .anyMatch(schedule ->
                        activity
                            .getSchedule()
                            .stream()
                            .anyMatch(scheduleRequestDTO ->
                                isIntervalInInterval(
                                    schedule.getStartDateTime(),
                                    schedule.getEndDateTime(),
                                    scheduleRequestDTO.getStartDateTime(),
                                    scheduleRequestDTO.getEndDateTime()
                                )
                            )
                    ) &&
                    activity1.getPlace() != null &&
                    activity.getPlace() != null &&
                    activity1.getPlace().getId().equals(activity.getPlace().getId())
            )) {
            throw new ControllerException(BAD_REQUEST, "Resource-Time Conflict!");
        }
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public ActivityResponseDTO create(@RequestBody ActivityRequestDTO activity) {
        try {
            prepareActivity(activity);
            return super.create(activity);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private void prepareActivity(ActivityRequestDTO activity) throws ControllerException {
        activity.getSpeakers().forEach(speakerActivity -> speakerActivity.setActivity(activity));
        activity.getSchedule().forEach(schedule -> schedule.setActivity(activity));
        if (activity.getTrack() == null || activity.getTrack().getId() == null) {
            throw new ControllerException(NOT_FOUND, TRACK_NOT_FOUND);
        }
        var track = trackService.findOne(activity.getTrack().getId());
        if (track == null) {
            throw new ControllerException(NOT_FOUND, TRACK_NOT_FOUND);
        }
        validateSchedule(activity, track);
        var activities = activityService.findAllByEdition(track.getEdition().getId());
        validatePlaceTimeConflict(activity, activities);
        var speaker = activity
            .getSpeakers()
            .stream()
            .allMatch(speakerActivityRequestDTO -> editionService.isSpeaker(
                activity.getTrack().getEdition().getId(),
                speakerActivityRequestDTO.getSpeaker().getId()
            ));
        if (!speaker) {
            throw new ControllerException(BAD_REQUEST, "The user is not a speaker");
        }
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public ActivityResponseDTO update(@RequestBody ActivityRequestDTO activity) {
        try {
            prepareActivity(activity);
            return super.update(activity);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @GetMapping("/{id}")
    public ActivityResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/edition/{editionId}")
    public List<ActivityResponseDTO> findAll(@PathVariable Long editionId) {
        return mapTo(activityService.findAllByEdition(editionId), ActivityResponseDTO.class);
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping("/filter")
    public Page<ActivityResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @GetMapping("/unique/edition/name")
    public Boolean verifyUniqueName(@RequestParam String name, @RequestParam Long editionId) {
        return activityService.findByNameAndEdition(name, editionId) != null;
    }

}
