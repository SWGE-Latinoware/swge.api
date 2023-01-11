package celtab.swge.controller;

import celtab.swge.dto.*;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.enums.ExclusionStatus;
import celtab.swge.model.user.DeleteRequest;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.model.user.User;
import celtab.swge.service.DeleteRequestService;
import celtab.swge.service.ExclusionService;
import celtab.swge.service.TutoredUserService;
import celtab.swge.service.UserService;
import celtab.swge.util.UUIDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/exclusions")
public class ExclusionController extends GenericController<Exclusion, Long, ExclusionRequestDTO, ExclusionResponseDTO> implements UUIDUtils {

    private final DeleteRequestService deleteRequestService;

    private final ExclusionService exclusionService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final TutoredUserService tutoredUserService;

    public ExclusionController(ExclusionService exclusionService, ModelMapper modelMapper, ObjectMapper objectMapper, DeleteRequestService deleteRequestService, PasswordEncoder passwordEncoder, UserService userService, TutoredUserService tutoredUserService) {
        super(exclusionService, modelMapper, objectMapper);
        this.deleteRequestService = deleteRequestService;
        this.exclusionService = exclusionService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tutoredUserService = tutoredUserService;
    }

    @Override
    @PostMapping
    @PreAuthorize("@webSecurity.isDPOorOwnUser(authentication, #exclusionRequestDTO.user)")
    @Transactional
    public ExclusionResponseDTO create(@RequestBody ExclusionRequestDTO exclusionRequestDTO) {
        if (exclusionRequestDTO.getUser() == null && exclusionRequestDTO.getTutoredUser() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "No Selected User");
        }
        var exclusions = exclusionService.findAllByUserId(exclusionRequestDTO.getUser().getId());
        if (Boolean.FALSE.equals(exclusions.isEmpty()) && exclusions.stream().anyMatch((exclusion -> exclusion.getStatus().equals(ExclusionStatus.APPROVED) || exclusion.getStatus().equals(ExclusionStatus.NOT_ANALYZED)))) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "There's already a Exclusion Request ongoing or Approved");
        }
        var savedDeleteRequest = deleteRequestService.save(mapTo(exclusionRequestDTO.getDeleteRequest(), DeleteRequest.class));
        exclusionRequestDTO.setDeleteRequest(mapTo(savedDeleteRequest, DeleteRequestRequestDTO.class));
        exclusionRequestDTO.setStatus(ExclusionStatus.NOT_ANALYZED);
        exclusionService.sendUserExclusionCreatedMail(exclusionRequestDTO.getUser(), Boolean.FALSE, savedDeleteRequest.getRequestType());
        exclusionService.sendUserExclusionCreatedMail(null, Boolean.TRUE, savedDeleteRequest.getRequestType());
        return super.create(exclusionRequestDTO);
    }

    @Override
    @PutMapping
    @PreAuthorize("@webSecurity.isDPOorOwnUser(authentication, #exclusionRequestDTO.user)")
    @Transactional
    public ExclusionResponseDTO update(@RequestBody ExclusionRequestDTO exclusionRequestDTO) {
        if (exclusionRequestDTO.getDpo() == null || exclusionRequestDTO.getStatus() == null || (exclusionRequestDTO.getTutoredUser() == null && exclusionRequestDTO.getUser() == null) || ((exclusionRequestDTO.getStatus().equals(ExclusionStatus.DENIED) || exclusionRequestDTO.getStatus().equals(ExclusionStatus.CANCELED)) && exclusionRequestDTO.getAttachment() == null) || exclusionRequestDTO.getReturnDate() == null || exclusionRequestDTO.getDeadlineExclusionDate() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "Missing mandatory field(s)");
        }
        return super.update(exclusionRequestDTO);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    @Transactional
    public void delete(@PathVariable Long id) {
        throw new ControllerException(HttpStatus.INTERNAL_SERVER_ERROR, "Method Not Allowed");
    }

    @Override
    @DeleteMapping
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        throw new ControllerException(HttpStatus.INTERNAL_SERVER_ERROR, "Method Not Allowed");
    }

    @Override
    @GetMapping
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    public Page<ExclusionResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @GetMapping("/{id}/requests")
    @PreAuthorize("@webSecurity.isOwnUser(authentication, #id)")
    public List<ExclusionResponseDTO> findAll(@PathVariable Long id) {
        return mapTo(exclusionService.findAllByUserId(id), ExclusionResponseDTO.class);
    }

    @Override
    @GetMapping("/filter")
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    public Page<ExclusionResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    public ExclusionResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    public User excludeUser(User user) {
        var generatedUUID = getRandomUUIDString();
        var generatedPassword = getRandomUUIDString();
        var encryptedPassword = passwordEncoder.encode(generatedPassword);

        user.setEmail(generatedUUID + "@removed.com");
        user.setPassword(encryptedPassword);
        user.setUserProfile(null);
        user.setAdmin(false);
        user.setGithub(null);
        user.setGithubId(null);
        user.setGoogleId(null);
        user.setOrcid(null);
        user.setLattes(null);
        user.setLinkedin(null);
        user.setWebsite(null);
        user.setBibliography(null);
        user.setAddressLine1("Av. Tancredo Neves, 6731 - Jardim Itaipu");
        user.setAddressLine2(null);
        user.setCity("Foz do Iguaçu");
        user.setZipCode("85867-900");
        user.setNeedsTypes(null);
        user.setOtherNeeds(null);
        user.setInstitution(null);
        user.setState("PR");
        user.setCountry("BR");
        user.setPhone(null);
        user.setCellPhone(null);
        user.setCompleted(Boolean.FALSE);
        user.setEnabled(Boolean.FALSE);
        user.setBirthDate(null);
        user.setName(generatedUUID);
        user.setTagName(generatedUUID);
        user.setConfirmed(Boolean.FALSE);
        user.setEmailCommunication(Boolean.FALSE);
        user.setSocialCommunication(Boolean.FALSE);

        return user;
    }

    private String createRandomData(Integer dataType) {
        char[] idNumber;
        var secureRandom = new SecureRandom();

        if (dataType == 0) {
            idNumber = new char[12];
            for (int i = 0; i < 12; i++) {
                idNumber[i] = (char) (secureRandom.nextInt());
            }
            return String.valueOf(idNumber).replaceAll("^(.{2})(.)(.{3})(.)(.{3})(.)(.)$", "$1.$3.$5-$7");
        } else {
            idNumber = new char[15];
            for (int i = 0; i < 15; i++) {
                idNumber[i] = (char) (secureRandom.nextInt());
            }
            return String.valueOf(idNumber).replaceAll("^(.)(.{2})(.)(.)(.{5})(.)(.{4})$", "($2) $5-$7");
        }
    }

    public TutoredUser randomizeTutoredUser(TutoredUser tutoredUser) {
        var randomUUID = getRandomUUIDString();
        tutoredUser.setName(randomUUID);
        tutoredUser.setTagName(randomUUID);
        tutoredUser.setCountry("BR");
        tutoredUser.setAuthorization(null);
        tutoredUser.setReviewer(null);
        tutoredUser.setOtherNeeds(null);
        tutoredUser.setNeedsTypes(null);
        tutoredUser.setBirthDate(Date.from(Instant.now()));
        tutoredUser.setIdNumber(createRandomData(0));
        tutoredUser.setCellPhone(createRandomData(1));

        return tutoredUser;
    }

    @PostMapping("/conclude")
    @PreAuthorize("@webSecurity.isDPO(authentication)")
    public ExclusionResponseDTO concludeExclusion(@RequestBody ExclusionRequestDTO exclusionRequestDTO) {
        if (exclusionRequestDTO.getEffectiveDeletionDate() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "No Effective Deletion Date was passed");
        }
        if (exclusionRequestDTO.getUser() == null && exclusionRequestDTO.getTutoredUser() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, "No selected user or tutored user");
        }
        if (exclusionRequestDTO.getUser() != null) {
            var user = userService.findOne(exclusionRequestDTO.getUser().getId());
            if (user == null) throw new ControllerException(HttpStatus.NOT_FOUND, "User not Found");
            var randomizeUser = mapTo(userService.save(excludeUser(user)), UserRequestDTO.class);
            exclusionRequestDTO.setUser(randomizeUser);
        } else if (exclusionRequestDTO.getTutoredUser() != null) {
            var tutoredUser = tutoredUserService.findOne(exclusionRequestDTO.getTutoredUser().getId());
            if (tutoredUser == null) throw new ControllerException(HttpStatus.NOT_FOUND, "Tutored User not Found");
            var randomTutoredUser = mapTo(tutoredUserService.save(randomizeTutoredUser(tutoredUser)), TutoredUserRequestDTO.class);
            exclusionRequestDTO.setTutoredUser(randomTutoredUser);
        }

        var deleteRequest = exclusionRequestDTO.getDeleteRequest();
        deleteRequest.setApplicantContact(null);
        deleteRequest.setNote(null);

        var savedDeleteRequest = deleteRequestService.save(mapTo(deleteRequest, DeleteRequest.class));
        exclusionRequestDTO.setDeleteRequest(mapTo(savedDeleteRequest, DeleteRequestRequestDTO.class));

        return super.update(exclusionRequestDTO);
    }
}
