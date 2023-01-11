package celtab.swge.service;

import celtab.swge.model.*;
import celtab.swge.model.enums.UserRole;
import celtab.swge.model.user.User;
import celtab.swge.repository.EditionRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditionService extends GenericService<Edition, Long> {

    private final EditionRepository editionRepository;

    private final UserPermissionService userPermissionService;

    private final CertificateService certificateService;

    private final TrackService trackService;

    private final CaravanService caravanService;

    private final SpeakerActivityService speakerActivityService;

    private final ActivityService activityService;


    public EditionService(
        EditionRepository editionRepository,
        UserPermissionService userPermissionService,
        CertificateService certificateService,
        TrackService trackService,
        CaravanService caravanService,
        SpeakerActivityService speakerActivityService, ActivityService activityService) {
        super(editionRepository, "edition(s)", new GenericSpecification<>(Edition.class));
        this.editionRepository = editionRepository;
        this.userPermissionService = userPermissionService;
        this.certificateService = certificateService;
        this.trackService = trackService;
        this.caravanService = caravanService;
        this.speakerActivityService = speakerActivityService;
        this.activityService = activityService;
    }

    public List<User> findAllByUserRole(Long id, UserRole userRole) {
        return userPermissionService
            .findAllByEditionAndUserRole(id, userRole)
            .stream()
            .map(UserPermission::getUser)
            .collect(Collectors.toList());
    }

    public List<Caravan> findAllCaravans(Long id) {
        return caravanService.findAllByEdition(id);
    }

    public List<Activity> findAllActivities(Long id) {
        return activityService.findAllByEdition(id);
    }

    public List<Certificate> findAllCertificates(Long id) {
        return certificateService.findAllByEdition(id);
    }

    public List<Track> findAllTracks(Long id) {
        return trackService.findAllByEdition(id);
    }

    public List<User> findAllSpeakers(Long id) {
        return findAllByUserRole(id, UserRole.SPEAKER);
    }

    public List<Caravan> findAllCaravansByCoordinator(Long editionId, Long coordinatorId) {
        return caravanService.findAllByEditionAndCoordinator(editionId, coordinatorId);
    }

    public List<Activity> findAllActivitiesBySpeaker(Long editionId, Long speakerId) {
        return speakerActivityService
            .findAllByEditionAndSpeaker(editionId, speakerId)
            .stream()
            .map(SpeakerActivity::getActivity)
            .collect(Collectors.toList());
    }

    private boolean isUserRoleById(Long editionId, UserRole userRole, Long userId) {
        return !userPermissionService
            .findByEditionAndUserRoleAndUser(editionId, userRole, userId)
            .isEmpty();
    }

    public boolean isSpeaker(Long editionId, Long speakerId) {
        return isUserRoleById(editionId, UserRole.SPEAKER, speakerId);
    }

    public boolean isSecretary(Long editionId, Long secretaryId) {
        return isUserRoleById(editionId, UserRole.SECRETARY, secretaryId);
    }

    public boolean isCoordinator(Long editionId, Long coordinatorId) {
        return isUserRoleById(editionId, UserRole.CARAVAN_COORDINATOR, coordinatorId);
    }

    public boolean isGridCoordinator(Long editionId, Long gridCoordinatorId) {
        return isUserRoleById(editionId, UserRole.GRID_COORDINATOR, gridCoordinatorId);
    }

    public boolean isDPO(Long editionId, Long dpoId) {
        return isUserRoleById(editionId, UserRole.DPO, dpoId);
    }

    public Edition findByName(String name) {
        return editionRepository.findByNameEquals(name).orElse(null);
    }

    public Edition findByShortName(String shortName) {
        return editionRepository.findByShortNameEquals(shortName).orElse(null);
    }

    public Edition findByYear(Integer year) {
        return editionRepository.findByYear(year).orElse(null);
    }

}
