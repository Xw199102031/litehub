package com.litehub.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.litehub.exception.ApiException;
import com.litehub.model.ProjectRole;
import com.litehub.model.User;
import com.litehub.repository.ProjectRoleRepository;
import com.litehub.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthorizationService {

    @Inject
    ProjectRoleRepository projectRoleRepository;

    @Inject
    UserService userService;

    // Cache key: projectId:userId -> comma-separated permission names
    private final Cache<String, Set<String>> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(10_000)
            .build();

    private String key(Long projectId, Long userId) {
        return projectId + ":" + userId;
    }

    public boolean hasPermission(Long projectId, Long userId, String permissionName) {
        User u = userService.findById(userId);
        if (u == null) return false;

        if ("ADMIN".equalsIgnoreCase(u.role)) return true;

        String cacheKey = key(projectId, userId);
        Set<String> perms = cache.get(cacheKey, k -> loadPermissionsFor(projectId, userId));
        return perms.contains(permissionName);
    }

    private Set<String> loadPermissionsFor(Long projectId, Long userId) {
        ProjectRole pr = projectRoleRepository.findByProjectAndUser(projectId, userId);
        if (pr == null || pr.role == null) return Set.of();

        String sql = "select p.name from role_permission rp join permissions p on rp.permission_id = p.id where rp.role_id = ?1";
        @SuppressWarnings("unchecked")
        java.util.List<String> list = projectRoleRepository.find(sql, pr.role.id).list();
        return list.stream().collect(Collectors.toSet());
    }

    public void checkPermissionOrThrow(Long projectId, Long userId, String permissionName) {
        if (!hasPermission(projectId, userId, permissionName)) {
            throw new ApiException("forbidden", 403);
        }
    }

    public void invalidate(Long projectId, Long userId) {
        cache.invalidate(key(projectId, userId));
    }

    public void invalidateUser(Long userId) {
        // iterate keys and invalidate those for userId
        for (String k : cache.asMap().keySet()) {
            if (k.endsWith(":" + userId)) cache.invalidate(k);
        }
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }
}
