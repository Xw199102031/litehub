package com.litehub;

import com.litehub.service.AdminService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AdminServiceSmokeTest {

    @Inject
    AdminService adminService;

    @Test
    void createRolePermissionAndAssignToUser() {
        var role = adminService.createRole("TEST_ROLE", "test role");
        assertNotNull(role);

        var perm = adminService.createPermission("TEST_PERMISSION", "test perm");
        assertNotNull(perm);

        adminService.assignPermissionToRole(role.name, perm.name);

        // cleanup
        adminService.removePermissionFromRole(role.name, perm.name);
        adminService.deletePermission(perm.name);
        adminService.deleteRole(role.name);
    }
}
