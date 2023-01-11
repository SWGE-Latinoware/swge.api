package celtab.swge.service;

import celtab.swge.model.UserPermission;
import celtab.swge.model.enums.UserRole;
import celtab.swge.repository.UserPermissionRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionService extends GenericService<UserPermission, Long> {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionService(UserPermissionRepository userPermissionRepository) {
        super(userPermissionRepository, "User Permission(s)", new GenericSpecification<>(UserPermission.class));
        this.userPermissionRepository = userPermissionRepository;
    }

    public List<UserPermission> findAllByEditionAndUser(Long edition, Long userId) {
        return userPermissionRepository.findAllByEditionIdAndUserId(edition, userId);
    }

    public List<UserPermission> findAllByEditionAndUserRole(Long edition, UserRole userRole) {
        return userPermissionRepository.findAllByEditionIdAndUserRole(edition, userRole);
    }

    public List<UserPermission> findByEditionAndUserRoleAndUser(Long edition, UserRole userRole, Long userId) {
        return userPermissionRepository.findAllByEditionIdAndUserRoleAndUserId(edition, userRole, userId);
    }
}
