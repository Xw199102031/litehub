package com.litehub.repository;

import com.litehub.model.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRoleRepository implements PanacheRepository<UserRole> {
    public UserRole findByUserAndRole(Long userId, Long roleId) {
        return find("user.id = ?1 and role.id = ?2", userId, roleId).firstResult();
    }
}
