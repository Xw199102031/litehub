package com.litehub;

import com.litehub.model.Project;
import com.litehub.model.ProjectMember;
import com.litehub.model.Role;
import com.litehub.model.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ProjectMemberRoleTest {

    @Test
    @Transactional
    void canCreateProjectMemberWithRole() {
        User owner = new User();
        owner.username = "role-owner-" + System.nanoTime();
        owner.password_hash = "x";
        owner.email = owner.username + "@example.com";
        owner.role = "USER";
        owner.persist();

        User user2 = new User();
        user2.username = "role-user-" + System.nanoTime();
        user2.password_hash = "x";
        user2.email = user2.username + "@example.com";
        user2.role = "USER";
        user2.persist();

        Project project = new Project();
        project.name = "Role Project";
        project.owner = owner;
        project.persist();

        ProjectMember member = new ProjectMember();
        member.project = project;
        member.user = user2;
        member.role = Role.MAINTAINER.name();
        member.persist();

        assertEquals(Role.MAINTAINER.name(), member.role);
    }
}
