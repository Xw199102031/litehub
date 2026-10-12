package com.litehub.repository;

import com.litehub.model.PermissionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PermissionRepository implements PanacheRepository<PermissionEntity> {
    public PermissionEntity findByName(String name) {
        return find("name", name).firstResult();
    }
}
