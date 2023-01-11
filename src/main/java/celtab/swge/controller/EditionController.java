package celtab.swge.controller;

import celtab.swge.dto.*;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Edition;
import celtab.swge.model.EditionHome;
import celtab.swge.model.enums.Gender;
import celtab.swge.model.enums.UserRole;
import celtab.swge.service.EditionHomeService;
import celtab.swge.service.EditionService;
import celtab.swge.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/editions")
public class EditionController extends GenericController<Edition, Long, EditionRequestDTO, EditionResponseDTO> {

    private final EditionService editionService;

    private final EditionHomeService editionHomeService;

    private final RegistrationService registrationService;

    public EditionController(EditionService editionService, ModelMapper modelMapper, ObjectMapper objectMapper, EditionHomeService editionHomeService, RegistrationService registrationService) {
        super(editionService, modelMapper, objectMapper);
        this.editionService = editionService;
        this.editionHomeService = editionHomeService;
        this.registrationService = registrationService;
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public EditionResponseDTO create(@RequestBody EditionRequestDTO edition) {
        return super.create(edition);
    }

    @PostMapping(("/languageContent"))
    @AdministratorFilter
    public EditionHomeResponseDTO createHomeContent(@RequestBody EditionHomeRequestDTO editionHomeRequestDTO) {
        try {
            if (editionHomeService.findByEditionIdAndLanguage(editionHomeRequestDTO.getEdition().getId(), editionHomeRequestDTO.getLanguage()) != null) {
                throw new ControllerException(BAD_REQUEST, editionHomeService.getCustomModelNameMessage() + " Already exists!");
            }

            var editionHome = mapTo(editionHomeRequestDTO, EditionHome.class);
            editionHome.setId(null);
            return mapTo(editionHomeService.save(editionHome), EditionHomeResponseDTO.class);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/languageContent")
    @AdministratorFilter
    public EditionHomeResponseDTO updateHomeContent(@RequestBody EditionHomeRequestDTO editionHomeRequestDTO) {
        try {
            var editionHome = mapTo(editionHomeRequestDTO, EditionHome.class);
            if (editionHomeService.findOne(editionHome.getId()) == null) {
                throw new ControllerException(NOT_FOUND, editionHomeService.getCustomModelNameMessage() + " Not Found!");
            }
            return mapTo(editionHomeService.save(editionHome), EditionHomeResponseDTO.class);
        } catch (ControllerException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public EditionResponseDTO update(@RequestBody EditionRequestDTO edition) {
        return super.update(edition);
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
    @GetMapping
    public Page<EditionResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/list")
    public List<EditionResponseDTO> findAll() {
        return super.findAll();
    }

    @GetMapping("/{id}/info/caravans")
    public List<CaravanResponseDTO> findEditionCaravans(@PathVariable Long id) {
        var caravans = mapTo(editionService.findAllCaravans(id), CaravanResponseDTO.class);

        return caravans.stream().map(caravan -> {

            caravan.setCaravanEnrollments(null);
            caravan.setCoordinator(null);
            caravan.setCaravanTutoredEnrollments(null);

            return caravan;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}/info/certificates")
    public List<CertificateResponseDTO> findEditionCertificates(@PathVariable Long id) {
        return mapTo(editionService.findAllCertificates(id), CertificateResponseDTO.class);
    }

    @GetMapping("/{id}/info/tracks")
    public List<TrackResponseDTO> findEditionTracks(@PathVariable Long id) {
        return mapTo(editionService.findAllTracks(id), TrackResponseDTO.class);
    }

    @GetMapping("/{id}/info/registrations")
    public RegistrationStats findEditionRegistrationStats(@PathVariable Long id) {
        var registrations = registrationService.findAllByEditionId(id);
        var stats = new RegistrationStats();
        var genderCount = new HashMap<String, Integer>();
        var countryCount = new HashMap<String, Integer>();
        var cityCount = new HashMap<String, Integer>();
        var stateCount = new HashMap<String, Integer>();

        stats.setRegistrationsCount(registrations.size());
        registrations.forEach(registration -> {
            var user = registration.getUser();
            genderCount.put(user.getGender() == null ? Gender.UNDEFINED.name() : user.getGender().name(), genderCount.getOrDefault(user.getGender() == null ? Gender.UNDEFINED.name() : user.getGender().name(), 0) + 1);
            countryCount.put(user.getCountry(), countryCount.getOrDefault(user.getCountry(), 0) + 1);
            cityCount.put(user.getCity(), cityCount.getOrDefault(user.getCity(), 0) + 1);
            stateCount.put(user.getState(), stateCount.getOrDefault(user.getState(), 0) + 1);
        });

        stats.setGendersCount(genderCount);
        stats.setCitiesCount(cityCount);
        stats.setCountriesCount(countryCount);
        stats.setStatesCount(stateCount);

        return stats;
    }

    @GetMapping("/{id}/info/activities")
    public List<ActivitySimpleResponseDTO> findEditionActivities(@PathVariable Long id) {
        var activities = mapTo(editionService.findAllActivities(id), ActivityResponseDTO.class);
        var removedInfo = activities.stream().map(activity -> {
            activity.setSpeakers(activity.getSpeakers().stream().map(speaker -> {
                var simpleSpeaker = new UserResponseDTO();
                simpleSpeaker.setName(speaker.getSpeaker().getName());
                simpleSpeaker.setBibliography(speaker.getSpeaker().getBibliography());
                simpleSpeaker.setUserProfile(speaker.getSpeaker().getUserProfile());
                speaker.setSpeaker(simpleSpeaker);
                return speaker;
            }).collect(Collectors.toList()));
            return activity;
        }).collect(Collectors.toList());

        return mapTo(removedInfo, ActivitySimpleResponseDTO.class);
    }

    @Override
    @GetMapping("/filter")
    public Page<EditionResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public EditionResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/{id}/coordinators")
    public List<UserResponseDTO> findAllCoordinators(@PathVariable Long id) {
        return findAllByUserRole(id, UserRole.CARAVAN_COORDINATOR);
    }

    @GetMapping("/{id}/speakers")
    public List<UserResponseDTO> findAllSpeakers(@PathVariable Long id) {
        return findAllByUserRole(id, UserRole.SPEAKER);
    }

    @GetMapping("/{id}/grid-coordinators")
    public List<UserResponseDTO> findAllGridCoordinators(@PathVariable Long id) {
        return findAllByUserRole(id, UserRole.GRID_COORDINATOR);
    }

    @GetMapping("/{id}/certificates")
    public List<CertificateResponseDTO> findAllCertificates(@PathVariable Long id) {
        return mapTo(editionService.findAllCertificates(id), CertificateResponseDTO.class);
    }

    @GetMapping("/{id}/tracks")
    public List<TrackResponseDTO> findAllTracks(@PathVariable Long id) {
        return mapTo(editionService.findAllTracks(id), TrackResponseDTO.class);
    }

    @GetMapping("/{editionId}/speaker/{speakerId}/activities")
    public List<ActivityResponseDTO> findAllActivitiesBySpeaker(
        @PathVariable Long editionId,
        @PathVariable Long speakerId) {
        try {
            return mapTo(editionService.findAllActivitiesBySpeaker(editionId, speakerId), ActivityResponseDTO.class);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private List<UserResponseDTO> findAllByUserRole(Long id, UserRole userRole) throws ControllerException {
        try {
            return mapTo(editionService.findAllByUserRole(id, userRole), UserResponseDTO.class);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/coordinator/{coordinatorId}")
    public Boolean isCoordinator(@PathVariable Long editionId, @PathVariable Long coordinatorId) {
        try {
            return editionService.isCoordinator(editionId, coordinatorId);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/speaker/{speakerId}")
    public Boolean isSpeaker(@PathVariable Long editionId, @PathVariable Long speakerId) {
        try {
            return editionService.isSpeaker(editionId, speakerId);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/secretary/{secretaryId}")
    public Boolean isSecretary(@PathVariable Long editionId, @PathVariable Long secretaryId) {
        try {
            return editionService.isSecretary(editionId, secretaryId);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/dpo/{dpoId}")
    public Boolean isDPO(@PathVariable Long editionId, @PathVariable Long dpoId) {
        try {
            return editionService.isDPO(editionId, dpoId);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/grid-coordinator/{gridCoordinatorId}")
    public Boolean isGridCoordinator(@PathVariable Long editionId, @PathVariable Long gridCoordinatorId) {
        try {
            return editionService.isGridCoordinator(editionId, gridCoordinatorId);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/coordinator/{coordinatorId}/caravans")
    public List<CaravanResponseDTO> findAllCaravansByCoordinator(
        @PathVariable Long editionId,
        @PathVariable Long coordinatorId) {
        try {
            return mapTo(editionService.findAllCaravansByCoordinator(editionId, coordinatorId), CaravanResponseDTO.class);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/unique/name")
    public Boolean verifyUniqueName(@RequestParam String name) {
        return editionService.findByName(name) != null;
    }

    @GetMapping("/unique/short-name")
    public Boolean verifyUniqueShortName(@RequestParam String shortName) {
        return editionService.findByShortName(shortName) != null;
    }

    @GetMapping("/unique/year")
    public Boolean verifyUniqueYear(@RequestParam Integer year) {
        return editionService.findByYear(year) != null;
    }

    @GetMapping("/{editionId}/languageContent/{language}")
    public EditionHomeResponseDTO findLanguageContentByEditionAndLanguage(@PathVariable Long editionId, @PathVariable String language) {
        try {
            var editionHome = editionHomeService.findByEditionIdAndLanguage(editionId, language);
            if (editionHome == null) throw new ControllerException(NOT_FOUND, "No Home Content for this Language");
            return mapTo(editionHome, EditionHomeResponseDTO.class);
        } catch (ControllerException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{editionId}/languageContent")
    public List<EditionHomeResponseDTO> findAllLanguageContentByEdition(@PathVariable Long editionId) {
        try {
            var editionHomes = editionHomeService.findAllByEditionId(editionId);
            if (editionHomes == null || Boolean.TRUE.equals(editionHomes.isEmpty()))
                throw new ControllerException(NOT_FOUND, "No Home Content for this Edition");
            return mapTo(editionHomeService.findAllByEditionId(editionId), EditionHomeResponseDTO.class);
        } catch (ControllerException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Data
    public static class RegistrationStats {
        private Integer registrationsCount;
        private HashMap<String, Integer> gendersCount;
        private HashMap<String, Integer> countriesCount;
        private HashMap<String, Integer> citiesCount;
        private HashMap<String, Integer> statesCount;
    }
}
