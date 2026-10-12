package com.litehub.resource;

import com.litehub.model.PermissionEntity;
import com.litehub.model.RoleEntity;
import com.litehub.service.AdminService;
import com.litehub.exception.ApiResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class AdminResource {

    @Inject
    AdminService adminService;

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/roles")
    public Response listRoles() {
        List<RoleEntity> roles = adminService.listRoles();
        return Response.ok(ApiResponse.ok(roles)).build();
    }

    @POST
    @Path("/roles")
    public Response createRole(Map<String, String> body) {
        String name = body.get("name");
        String description = body.getOrDefault("description", "");
        RoleEntity r = adminService.createRole(name, description);
        return Response.status(Response.Status.CREATED).entity(ApiResponse.ok(r)).build();
    }

    @DELETE
    @Path("/roles/{name}")
    public Response deleteRole(@PathParam("name") String name) {
        adminService.deleteRole(name);
        return Response.noContent().build();
    }

    @GET
    @Path("/permissions")
    public Response listPermissions() {
        List<PermissionEntity> perms = adminService.listPermissions();
        return Response.ok(ApiResponse.ok(perms)).build();
    }

    @POST
    @Path("/permissions")
    public Response createPermission(Map<String, String> body) {
        String name = body.get("name");
        String description = body.getOrDefault("description", "");
        PermissionEntity p = adminService.createPermission(name, description);
        return Response.status(Response.Status.CREATED).entity(ApiResponse.ok(p)).build();
    }

    @POST
    @Path("/roles/{roleName}/permissions")
    public Response assignPermissionToRole(@PathParam("roleName") String roleName, Map<String, String> body) {
        String permission = body.get("permission");
        adminService.assignPermissionToRole(roleName, permission);
        return Response.ok(ApiResponse.ok()).build();
    }

    @DELETE
    @Path("/roles/{roleName}/permissions/{permission}")
    public Response removePermissionFromRole(@PathParam("roleName") String roleName, @PathParam("permission") String permission) {
        adminService.removePermissionFromRole(roleName, permission);
        return Response.noContent().build();
    }

    @POST
    @Path("/users/{userId}/roles")
    public Response assignRoleToUser(@PathParam("userId") Long userId, Map<String, String> body) {
        String role = body.get("role");
        adminService.assignRoleToUser(role, userId);
        return Response.ok(ApiResponse.ok()).build();
    }

    @DELETE
    @Path("/users/{userId}/roles/{role}")
    public Response removeRoleFromUser(@PathParam("userId") Long userId, @PathParam("role") String role) {
        adminService.removeRoleFromUser(role, userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/projects/{projectId}/roles")
    public Response assignRoleToProject(@PathParam("projectId") Long projectId, Map<String, Object> body) {
        Number userIdNum = (Number) body.get("userId");
        String role = (String) body.get("role");
        Long userId = userIdNum.longValue();
        adminService.assignRoleToProject(role, projectId, userId);
        return Response.ok(ApiResponse.ok()).build();
    }

    @DELETE
    @Path("/projects/{projectId}/roles/{userId}")
    public Response removeRoleFromProject(@PathParam("projectId") Long projectId, @PathParam("userId") Long userId) {
        adminService.removeRoleFromProject(projectId, userId);
        return Response.noContent().build();
    }
}
