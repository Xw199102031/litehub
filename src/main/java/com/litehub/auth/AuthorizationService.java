package com.litehub.auth;

import com.litehub.exception.ApiException;
import com.litehub.model.ProjectRole;
import com.litehub.model.User;
import com.litehub.repository.ProjectRoleRepository;
import com.litehub.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthorizationService {

    @Inject
    ProjectRoleRepository projectRoleRepository;

    @Inject
    UserService userService;

    public boolean hasPermission(Long projectId, Long userId, String permissionName) {
        User u = userService.findById(userId);
        if (u == null) return false;

        if ("ADMIN".equalsIgnoreCase(u.role)) {
            return true;
        }

        ProjectRole pr = projectRoleRepository.findByProjectAndUser(projectId, userId);
        if (pr == null || pr.role == null) {
            return false;
        }

        String sql =
            "select p.name from role_permission rp " +
            "join permissions p on rp.permission_id = p.id " +
            "where rp.role_id = ?1 and p.name = ?2";

        Object result = projectRoleRepository.find(sql, pr.role.id, permissionName).firstResult();
        return result != null;
    }

    public void checkPermissionOrThrow(Long projectId, Long userId, String permissionName) {
        if (!hasPermission(projectId, userId, permissionName)) {
            throw new ApiException("forbidden", 403);
        }
    }
}
