package celtab.swge.security;

import celtab.swge.dto.UserRequestDTO;
import celtab.swge.model.enums.UserRole;
import celtab.swge.model.user.User;
import celtab.swge.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WebSecurity {

    @Autowired
    private CaravanService caravanService;

    @Autowired
    private UserService userService;

    @Autowired
    private CaravanEnrollmentService caravanEnrollmentService;

    @Autowired
    private CaravanTutoredEnrollmentService caravanTutoredEnrollmentService;

    @Autowired
    private RegistrationService registrationService;

    public boolean isAdministrator(Authentication authentication) {
        var user = getPrincipal(authentication);
        if (user == null) return false;
        return Boolean.TRUE.equals(user.getAdmin());
    }

    public boolean isAdministratorOrSecretary(Authentication authentication) {
        return isAdministrator(authentication) || isSecretary(authentication);
    }

    public boolean isSecretary(Authentication authentication) {
        var user = getPrincipal(authentication);
        if (user == null) return false;
        return Boolean.TRUE.equals(user.getUserPermissions().stream().anyMatch(userPermission -> userPermission.getUserRole().equals(UserRole.SECRETARY)));
    }

    public boolean isDPO(Authentication authentication) {
        var user = getPrincipal(authentication);
        if (user == null) return false;
        return Boolean.TRUE.equals(user.getUserPermissions().stream().anyMatch(userPermission -> userPermission.getUserRole().equals(UserRole.DPO)));
    }

    public boolean isAdministratorOrOwnCaravanCoordinator(Authentication authentication, Long caravanId) {
        if (isAdministrator(authentication)) return true;
        var caravan = caravanService.findOne(caravanId);
        if (caravan == null) return false;
        var user = getPrincipal(authentication);
        if (user == null) return false;
        try {
            return Objects.equals(caravan.getCoordinator().getId(), user.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdministratorOrOwnCaravanCoordinatorEnrollment(Authentication authentication, List<Long> caravanEnrollmentIds) {
        try {
            var caravanIds = caravanEnrollmentIds
                .stream()
                .map(enrollId -> caravanEnrollmentService.findOne(enrollId).getCaravan().getId())
                .collect(Collectors.toList());
            return caravanIds.stream().allMatch(id -> isAdministratorOrOwnCaravanCoordinator(authentication, id));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdministratorOrOwnCaravanCoordinatorTutoredEnrollment(Authentication authentication, List<Long> caravanEnrollmentIds) {
        try {
            var caravanIds = caravanEnrollmentIds
                .stream()
                .map(enrollId -> caravanTutoredEnrollmentService.findOne(enrollId).getCaravan().getId())
                .collect(Collectors.toList());
            return caravanIds.stream().allMatch(id -> isAdministratorOrOwnCaravanCoordinator(authentication, id));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdministratorOrOwnCaravanCoordinatorOrOwnCompletedUser(
        Authentication authentication,
        Long caravanId,
        Long userId
    ) {
        if (isAdministratorOrOwnCaravanCoordinator(authentication, caravanId)) return true;
        return isOwnCompletedUser(authentication, userId);
    }

    public boolean isAdministratorOrOwnUserRegistration(Authentication authentication, Long registrationId) {
        var registration = registrationService.findOne(registrationId);
        if (registration == null) return false;
        return isAdministratorOrOwnUser(authentication, registration.getUser().getId());
    }

    public boolean isAdministratorOrOwnCompletedUserRegistration(Authentication authentication, Long registrationId) {
        var registration = registrationService.findOne(registrationId);
        if (registration == null) return false;
        return isAdministratorOrOwnCompletedUser(authentication, registration.getUser().getId());
    }

    public boolean isAdministratorOrOwnUser(Authentication authentication, Long userId) {
        if (isAdministrator(authentication)) return true;
        return isOwnUser(authentication, userId);
    }

    public boolean isAdministratorOrOwnUserOrDPO(Authentication authentication, Long userId) {
        if (isAdministrator(authentication)) return true;
        if (isDPO(authentication)) return true;
        return isOwnUser(authentication, userId);
    }

    public boolean isAdministratorOrOwnCompletedUser(Authentication authentication, Long userId) {
        if (isAdministrator(authentication)) return true;
        return isOwnCompletedUser(authentication, userId);
    }

    public boolean isAdministratorOrOwnUserEmailOrDPO(Authentication authentication, String email) {
        if (isAdministrator(authentication)) return true;
        if (isDPO(authentication)) return true;
        var user = getPrincipal(authentication);
        if (user == null) return false;
        return Objects.equals(user.getEmail(), email);
    }

    public boolean isOwnUser(Authentication authentication, Long userId) {
        var user = getPrincipal(authentication);
        if (user == null) return false;
        return Objects.equals(user.getId(), userId);
    }

    public boolean isDPOorOwnUser(Authentication authentication, UserRequestDTO user) {
        if (user == null) return isDPO(authentication);
        return isDPO(authentication) || isOwnUser(authentication, user.getId());
    }

    public boolean isOwnCompletedUser(Authentication authentication, Long userId) {
        var user = getPrincipal(authentication);
        if (user == null) return false;
        if (!Objects.equals(user.getId(), userId)) return false;
        return user.getCompleted();
    }

    private User getPrincipal(Authentication authentication) {
        try {
            return userService.findByEmail((String) authentication.getPrincipal());
        } catch (Exception e) {
            return null;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAuthority(T(celtab.swge.security.SecurityRoles).ADMINISTRATOR_ROLE_VALUE)")
    public @interface AdministratorFilter {
    }

}
