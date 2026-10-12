package com.litehub.repository;

import com.litehub.model.RoleEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<RoleEntity> {
    public RoleEntity findByName(String name) {
        return find("name", name).firstResult();
    }
}
