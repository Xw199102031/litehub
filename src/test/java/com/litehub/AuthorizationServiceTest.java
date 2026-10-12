package com.litehub;

import com.litehub.auth.AuthorizationService;
import com.litehub.model.Permission;
import com.litehub.model.Project;
import com.litehub.model.ProjectMember;
import com.litehub.model.Role;
import com.litehub.model.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AuthorizationServiceTest {

    @Inject
    AuthorizationService authorizationService;

    @Test
    @Transactional
    void ownerHasProjectWritePermission() {
        User owner = new User();
        owner.username = "owner-user-" + System.nanoTime();
        owner.password_hash = "x";
        owner.email = owner.username + "@example.com";
        owner.role = "USER";
        owner.persist();

        Project project = new Project();
        project.name = "Owner Project";
        project.owner = owner;
        project.persist();

        ProjectMember member = new ProjectMember();
        member.project = project;
        member.user = owner;
        member.role = Role.OWNER.name();
        member.persist();

        assertTrue(authorizationService.hasPermission(project.id, owner.id, Permission.PROJECT_WRITE));
        assertTrue(authorizationService.hasPermission(project.id, owner.id, Permission.TASK_CREATE));
    }

    @Test
    @Transactional
    void viewerCannotDeleteProject() {
        User owner = new User();
        owner.username = "viewer-owner-" + System.nanoTime();
        owner.password_hash = "x";
        owner.email = owner.username + "@example.com";
        owner.role = "USER";
        owner.persist();

        User viewer = new User();
        viewer.username = "viewer-user-" + System.nanoTime();
        viewer.password_hash = "x";
        viewer.email = viewer.username + "@example.com";
        viewer.role = "USER";
        viewer.persist();

        Project project = new Project();
        project.name = "Viewer Project";
        project.owner = owner;
        project.persist();

        ProjectMember member = new ProjectMember();
        member.project = project;
        member.user = viewer;
        member.role = Role.VIEWER.name();
        member.persist();

        assertFalse(authorizationService.hasPermission(project.id, viewer.id, Permission.PROJECT_DELETE));
    }

    @Test
    @Transactional
    void adminHasAllPermissions() {
        User admin = new User();
        admin.username = "admin-user-" + System.nanoTime();
        admin.password_hash = "x";
        admin.email = admin.username + "@example.com";
        admin.role = "ADMIN";
        admin.persist();

        Project project = new Project();
        project.name = "Admin Project";
        project.owner = admin;
        project.persist();

        assertTrue(authorizationService.hasPermission(project.id, admin.id, Permission.PROJECT_DELETE));
        assertTrue(authorizationService.hasPermission(project.id, admin.id, Permission.TASK_DELETE));
        assertTrue(authorizationService.hasPermission(project.id, admin.id, Permission.MEMBER_MANAGE));
    }
}
