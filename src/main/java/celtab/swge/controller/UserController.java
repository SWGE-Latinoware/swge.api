package celtab.swge.controller;

import celtab.swge.auth.oauth2.OAuth2UserInfoFactory;
import celtab.swge.dto.UserRequestDTO;
import celtab.swge.dto.UserResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.enums.AuthProvider;
import celtab.swge.model.enums.ExclusionStatus;
import celtab.swge.model.enums.RequestType;
import celtab.swge.model.user.DeleteRequest;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.User;
import celtab.swge.service.DeleteRequestService;
import celtab.swge.service.ExclusionService;
import celtab.swge.service.FileService;
import celtab.swge.service.UserService;
import celtab.swge.util.ClassPathUtils;
import celtab.swge.util.UUIDUtils;
import celtab.swge.util.email.EmailOptions;
import celtab.swge.util.email.EmailSender;
import celtab.swge.util.template_processor.EmailTemplateProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/users")
public class UserController extends GenericController<User, Long, UserRequestDTO, UserResponseDTO> implements ClassPathUtils, OAuth2UserInfoFactory, UUIDUtils {

    private static final String NOT_FOUND_MSG = " Not found!";

    private static final String USER_NOT_FOUND_MSG = "User Not Found!";

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final FileService fileService;

    private final EmailSender emailSender;

    private final EmailTemplateProcessor emailTemplateProcessor;

    private final DeleteRequestService deleteRequestService;

    private final ExclusionService exclusionService;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public UserController(
        UserService userService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper,
        PasswordEncoder passwordEncoder,
        FileService fileService, EmailSender emailSender,
        EmailTemplateProcessor emailTemplateProcessor, DeleteRequestService deleteRequestService, ExclusionService exclusionService) {
        super(userService, modelMapper, objectMapper);
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.fileService = fileService;
        this.emailSender = emailSender;
        this.emailTemplateProcessor = emailTemplateProcessor;
        this.deleteRequestService = deleteRequestService;
        this.exclusionService = exclusionService;
    }

    @PostMapping("/reset-password")
    @Transactional
    public void resetPassword(@RequestParam String email) {
        try {
            var realUser = getUserByEmail(email);
            var defaultPassword = getRandomUUIDString();
            var encryptedPassword = passwordEncoder.encode(defaultPassword);
            realUser.setPassword(encryptedPassword);
            realUser.setAlterPassword(true);
            userService.save(realUser);
            sendResetPasswordEmail(realUser, defaultPassword);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private void sendResetPasswordEmail(User user, String tempPassword) throws ControllerException {
        try {
            var options = new EmailOptions();
            options.setFrom(emailFrom);
            options.setTo(user.getEmail());
            options.setSubject("Reset de Senha");
            options.setIsTextHTML(true);
            var html = getContentFromClassPath("mail/passwordReset.html");
            emailTemplateProcessor.setTo(user);
            emailTemplateProcessor.setTempPassword(tempPassword);
            emailTemplateProcessor.setEdition(null);
            options.setText(emailTemplateProcessor.processTemplate(html));
            emailSender.sendEmail(options);
        } catch (Exception e) {
            throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private UserResponseDTO sendEmailConfirmation(UserResponseDTO user) throws ControllerException {
        try {
            var options = new EmailOptions();
            options.setFrom(emailFrom);
            options.setTo(user.getEmail());
            options.setSubject("Confirmação de E-mail");
            options.setIsTextHTML(true);
            var html = getContentFromClassPath("mail/emailConfirmation.html");
            emailTemplateProcessor.setTo(mapTo(user, User.class));
            emailTemplateProcessor.setEdition(null);
            options.setText(emailTemplateProcessor.processTemplate(html));
            emailSender.sendEmail(options);
            return user;
        } catch (Exception e) {
            throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validateUserCompleted(UserRequestDTO user) throws ControllerException {
        if (user.getCountry() == null ||
            user.getCountry().isBlank() ||
            user.getZipCode() == null ||
            user.getZipCode().isBlank() ||
            user.getState() == null ||
            user.getState().isBlank() ||
            user.getCity() == null ||
            user.getCity().isBlank() ||
            user.getAddressLine1() == null ||
            user.getAddressLine1().isBlank() ||
            isFakeTempEmailForJwt(user.getEmail()) ||
            isFakeTempName(user.getName()) ||
            isFakeTempName(user.getTagName())
        ) {
            throw new ControllerException(BAD_REQUEST, "Incomplete User!");
        }
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public UserResponseDTO create(@RequestBody UserRequestDTO user) {
        try {
            var defaultPassword = getRandomUUIDString();
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.getUserPermissions().forEach(userPermission -> userPermission.setUser(user));
            user.setSocialCommunication(Boolean.FALSE);
            validateUserCompleted(user);
            var responseUser = sendEmailConfirmation(super.create(user));
            sendResetPasswordEmail(mapTo(responseUser, User.class), defaultPassword);
            return responseUser;
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private void verifyUserAndSendEmailConfirmation(User user) throws ControllerException {
        if (user == null) {
            throw new ControllerException(NOT_FOUND, USER_NOT_FOUND_MSG);
        }
        sendEmailConfirmation(mapTo(user, UserResponseDTO.class));
    }

    @PostMapping("/email-confirmation/{id}")
    public void resendEmail(@PathVariable Long id) {
        var user = userService.findOne(id);
        verifyUserAndSendEmailConfirmation(user);
    }

    @PostMapping("/email-confirmation/email")
    public void resendEmail(@RequestParam String email) {
        var user = getUserByEmail(email);
        verifyUserAndSendEmailConfirmation(user);
    }

    private User getUserByEmail(String email) throws ControllerException {
        var user = userService.findByEmail(email);
        if (user == null) {
            throw new ControllerException(NOT_FOUND, USER_NOT_FOUND_MSG);
        }
        return user;
    }

    @GetMapping("/validate/email-confirmation")
    public boolean validateEmailConfirmation(@RequestParam String email) {
        return getUserByEmail(email).getConfirmed();
    }

    @GetMapping("/validate/user-disabled")
    public boolean validateUserDisabled(@RequestParam String email) {
        return getUserByEmail(email).getEnabled();
    }

    @PostMapping("/auto-registration")
    @Transactional
    public UserResponseDTO createAuto(@RequestBody UserRequestDTO user) {
        try {
            var encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            user.setUserPermissions(Collections.emptyList());
            user.setSocialCommunication(Boolean.FALSE);
            validateUserCompleted(user);
            return sendEmailConfirmation(super.create(user));
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    private void shouldRemoveProfile(User oldUserInstance, User newUserInstance) throws ServiceException {
        if (oldUserInstance.getUserProfile() != null && newUserInstance.getUserProfile() == null) {
            fileService.removeFile(oldUserInstance.getUserProfile().getId());
        }
    }

    @PutMapping("/admin")
    @AdministratorFilter
    @Transactional
    public UserResponseDTO updateByAdmin(@RequestBody UserRequestDTO user) {
        try {
            user.getUserPermissions().forEach(userPermission -> userPermission.setUser(user));
            var realUser = mapTo(user, User.class);
            var oldUser = service.findOne(realUser.getId());
            if (oldUser == null) {
                throw new ControllerException(NOT_FOUND, service.getCustomModelNameMessage() + NOT_FOUND_MSG);
            }

            shouldRemoveProfile(oldUser, realUser);

            realUser.setPassword(oldUser.getPassword());
            realUser.setGithubId(oldUser.getGithubId());
            realUser.setGoogleId(oldUser.getGoogleId());
            realUser.setAlterPassword(oldUser.getAlterPassword());
            validateUserCompleted(user);
            return mapTo(service.save(realUser), UserResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/user")
    @PreAuthorize("@webSecurity.isOwnUser(authentication, #user.id)")
    @Transactional
    public UserResponseDTO updateByUser(@RequestBody UserRequestDTO user) {
        try {
            var realUser = mapTo(user, User.class);
            var oldUser = service.findOne(realUser.getId());

            shouldRemoveProfile(oldUser, realUser);

            if (realUser.getPassword() != null) {
                var encryptedPassword = passwordEncoder.encode(realUser.getPassword());
                realUser.setPassword(encryptedPassword);
                realUser.setAlterPassword(false);
            } else {
                realUser.setPassword(oldUser.getPassword());
                realUser.setAlterPassword(oldUser.getAlterPassword());
            }

            realUser.setGithubId(oldUser.getGithubId());
            realUser.setGoogleId(oldUser.getGoogleId());
            realUser.setUserPermissions(oldUser.getUserPermissions());
            validateUserCompleted(user);
            return mapTo(service.save(realUser), UserResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("@webSecurity.isDPOorOwnUser(authentication, #id)")
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
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/filter")
    public Page<UserResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserOrDPO(authentication, #id)")
    public UserResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/email")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserEmailOrDPO(authentication, #email)")
    public UserResponseDTO findOneByEmail(@RequestParam String email) {
        var user = userService.findByEmail(email);
        if (user == null) {
            throw new ControllerException(NOT_FOUND, USER_NOT_FOUND_MSG);
        }
        return mapTo(user, UserResponseDTO.class);
    }

    @PutMapping("/change-enable/{id}")
    @AdministratorFilter
    @Transactional
    public UserResponseDTO enableUser(@PathVariable Long id) {
        try {
            var user = userService.findOne(id);
            if (user == null) {
                throw new ControllerException(NOT_FOUND, USER_NOT_FOUND_MSG);
            }
            user.setEnabled(!user.getEnabled());
            var newUser = userService.save(user);
            return mapTo(newUser, UserResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, "It was not possible to enable/disable the user!");
        }
    }

    @PutMapping("/social-login/untie/{registrationId}/{id}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUser(authentication, #id)")
    @Transactional
    public UserResponseDTO untieSocialAccount(@PathVariable Long id, @PathVariable String registrationId) {
        try {
            var user = userService.findOne(id);
            if (user == null) {
                throw new ControllerException(NOT_FOUND, USER_NOT_FOUND_MSG);
            }
            if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
                user.setGoogleId(null);
            } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
                user.setGithubId(null);
            } else {
                throw new ControllerException(BAD_REQUEST, "Login method not supported for this user: " + registrationId);
            }
            var newUser = userService.save(user);
            return mapTo(newUser, UserResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, "It was not possible to untie the social account!");
        }
    }

    @GetMapping("/unique/email")
    public Boolean verifyUniqueEmail(@RequestParam String email) {
        return userService.findByEmail(email) != null;
    }

    @GetMapping("/unique/name")
    public Boolean verifyUniqueName(@RequestParam String name) {
        return userService.findByName(name) != null;
    }

    @GetMapping("/unique/tag-name")
    public Boolean verifyUniqueTagName(@RequestParam String tagName) {
        return userService.findByTagName(tagName) != null;
    }

    @Transactional
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void checkUsersInactivity() {
        var users = userService.findAll().stream().filter(user -> user.getLastLogin() != null && (user.getExclusionRequests() == null || user.getExclusionRequests().isEmpty() || user.getExclusionRequests().stream().anyMatch(exclusion -> exclusion.getStatus() != null && (exclusion.getStatus().equals(ExclusionStatus.DENIED) || exclusion.getStatus().equals(ExclusionStatus.CANCELED)))));
        users.forEach(user -> {
            if (ChronoUnit.MONTHS.between(LocalDate.ofInstant(user.getLastLogin().toInstant(), ZoneId.systemDefault()), LocalDate.now()) >= 18) {
                var deleteRequest = new DeleteRequest();
                deleteRequest.setRequestDate(Date.from(Instant.now()));
                deleteRequest.setRequestType(RequestType.INACTIVITY);
                var savedDeleteRequest = deleteRequestService.save(deleteRequest);
                var exclusion = new Exclusion();
                exclusion.setUser(user);
                exclusion.setDeleteRequest(savedDeleteRequest);
                exclusion.setRegistryDate(Date.from(Instant.now()));
                exclusion.setStatus(ExclusionStatus.NOT_ANALYZED);
                exclusionService.sendUserExclusionCreatedMail(mapTo(user, UserRequestDTO.class), Boolean.FALSE, deleteRequest.getRequestType());
                exclusionService.sendUserExclusionCreatedMail(null, Boolean.TRUE, deleteRequest.getRequestType());
                exclusionService.save(exclusion);
            }
        });
    }
}
