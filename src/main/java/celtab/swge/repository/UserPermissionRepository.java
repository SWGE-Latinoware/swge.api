package celtab.swge.repository;

import celtab.swge.model.UserPermission;
import celtab.swge.model.enums.UserRole;

import java.util.List;

public interface UserPermissionRepository extends GenericRepository<UserPermission, Long> {

    List<UserPermission> findAllByEditionIdAndUserId(Long editionId, Long userId);

    List<UserPermission> findAllByEditionIdAndUserRole(Long editionId, UserRole userRole);

    List<UserPermission> findAllByEditionIdAndUserRoleAndUserId(Long editionId, UserRole userRole, Long userId);
}
