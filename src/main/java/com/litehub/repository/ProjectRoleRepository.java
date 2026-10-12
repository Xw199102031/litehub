package com.litehub.repository;

import com.litehub.model.ProjectRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectRoleRepository implements PanacheRepository<ProjectRole> {
    public ProjectRole findByProjectAndUser(Long projectId, Long userId) {
        return find("project.id = ?1 and user.id = ?2", projectId, userId).firstResult();
    }
}
