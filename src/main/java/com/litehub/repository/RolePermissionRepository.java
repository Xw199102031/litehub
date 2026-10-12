package com.litehub.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class RolePermissionRepository {

    @Inject
    EntityManager em;

    public void addPermissionToRole(Long roleId, Long permissionId) {
        em.createNativeQuery("INSERT INTO role_permission (role_id, permission_id) VALUES (?1, ?2) ON CONFLICT DO NOTHING")
          .setParameter(1, roleId)
          .setParameter(2, permissionId)
          .executeUpdate();
    }

    public void removePermissionFromRole(Long roleId, Long permissionId) {
        em.createNativeQuery("DELETE FROM role_permission WHERE role_id = ?1 AND permission_id = ?2")
          .setParameter(1, roleId)
          .setParameter(2, permissionId)
          .executeUpdate();
    }

    public boolean roleHasPermission(Long roleId, String permissionName) {
        Object r = em.createNativeQuery(
            "select 1 from role_permission rp join permissions p on rp.permission_id = p.id where rp.role_id = ?1 and p.name = ?2")
            .setParameter(1, roleId)
            .setParameter(2, permissionName)
            .getResultStream().findFirst().orElse(null);
        return r != null;
    }
}
