package com.litehub.service;

import com.litehub.model.PermissionEntity;
import com.litehub.model.ProjectRole;
import com.litehub.model.RoleEntity;
import com.litehub.model.UserRole;
import com.litehub.repository.PermissionRepository;
import com.litehub.repository.ProjectRoleRepository;
import com.litehub.repository.RolePermissionRepository;
import com.litehub.repository.RoleRepository;
import com.litehub.repository.UserRoleRepository;
import com.litehub.repository.UserRepository;
import com.litehub.model.User;
import com.litehub.exception.ApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AdminService {

    @Inject
    RoleRepository roleRepository;

    @Inject
    PermissionRepository permissionRepository;

    @Inject
    RolePermissionRepository rolePermissionRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    UserRoleRepository userRoleRepository;

    @Inject
    ProjectRoleRepository projectRoleRepository;

    @Inject
    AuthorizationService authorizationService;

    public List<RoleEntity> listRoles() {
        return roleRepository.listAll();
    }

    public List<PermissionEntity> listPermissions() {
        return permissionRepository.listAll();
    }

    @Transactional
    public RoleEntity createRole(String name, String description) {
        if (roleRepository.findByName(name) != null) {
            throw new ApiException("role already exists", 409);
        }
        RoleEntity r = new RoleEntity();
        r.name = name.trim().toUpperCase();
        r.description = description;
        roleRepository.persist(r);
        return r;
    }

    @Transactional
    public void deleteRole(String name) {
        RoleEntity r = roleRepository.findByName(name);
        if (r == null) throw new ApiException("role not found", 404);
        roleRepository.delete(r);
        authorizationService.invalidateAll();
    }

    @Transactional
    public PermissionEntity createPermission(String name, String description) {
        if (permissionRepository.findByName(name) != null) {
            throw new ApiException("permission already exists", 409);
        }
        PermissionEntity p = new PermissionEntity();
        p.name = name.trim().toUpperCase();
        p.description = description;
        permissionRepository.persist(p);
        return p;
    }

    @Transactional
    public void deletePermission(String name) {
        PermissionEntity p = permissionRepository.findByName(name);
        if (p == null) throw new ApiException("permission not found", 404);
        permissionRepository.delete(p);
        authorizationService.invalidateAll();
    }

    @Transactional
    public void assignPermissionToRole(String roleName, String permissionName) {
        RoleEntity r = roleRepository.findByName(roleName);
        PermissionEntity p = permissionRepository.findByName(permissionName);
        if (r == null || p == null) throw new ApiException("role or permission not found", 404);
        rolePermissionRepository.addPermissionToRole(r.id, p.id);
        authorizationService.invalidateAll();
    }

    @Transactional
    public void removePermissionFromRole(String roleName, String permissionName) {
        RoleEntity r = roleRepository.findByName(roleName);
        PermissionEntity p = permissionRepository.findByName(permissionName);
        if (r == null || p == null) throw new ApiException("role or permission not found", 404);
        rolePermissionRepository.removePermissionFromRole(r.id, p.id);
        authorizationService.invalidateAll();
    }

    @Transactional
    public void assignRoleToUser(String roleName, Long userId) {
        RoleEntity r = roleRepository.findByName(roleName);
        User u = userRepository.findById(userId);
        if (r == null || u == null) throw new ApiException("role or user not found", 404);
        UserRole ur = userRoleRepository.findByUserAndRole(userId, r.id);
        if (ur != null) return; // already assigned
        UserRole newUr = new UserRole();
        newUr.user = u;
        newUr.role = r;
        userRoleRepository.persist(newUr);
        authorizationService.invalidateUser(userId);
    }

    @Transactional
    public void removeRoleFromUser(String roleName, Long userId) {
        RoleEntity r = roleRepository.findByName(roleName);
        if (r == null) throw new ApiException("role not found", 404);
        UserRole ur = userRoleRepository.findByUserAndRole(userId, r.id);
        if (ur == null) return;
        userRoleRepository.delete(ur);
        authorizationService.invalidateUser(userId);
    }

    @Transactional
    public void assignRoleToProject(String roleName, Long projectId, Long userId) {
        RoleEntity r = roleRepository.findByName(roleName);
        if (r == null) throw new ApiException("role not found", 404);
        // check existing
        ProjectRole existing = projectRoleRepository.findByProjectAndUser(projectId, userId);
        if (existing != null) {
            existing.role = r;
            projectRoleRepository.persist(existing);
        } else {
            ProjectRole pr = new ProjectRole();
            pr.project = com.litehub.model.Project.findById(projectId);
            pr.user = userRepository.findById(userId);
            if (pr.project == null || pr.user == null) throw new ApiException("project or user not found", 404);
            pr.role = r;
            projectRoleRepository.persist(pr);
        }
        authorizationService.invalidate(projectId, userId);
    }

    @Transactional
    public void removeRoleFromProject(Long projectId, Long userId) {
        ProjectRole pr = projectRoleRepository.findByProjectAndUser(projectId, userId);
        if (pr == null) return;
        projectRoleRepository.delete(pr);
        authorizationService.invalidate(projectId, userId);
    }
}
