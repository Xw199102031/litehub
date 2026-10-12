package com.litehub.resource;

import com.litehub.auth.CasbinService;
import com.litehub.exception.ApiResponse;
import com.litehub.model.User;
import com.litehub.service.AuditLogService;
import com.litehub.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/admin/casbin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class CasbinResource {

    @Inject
    CasbinService casbinService;

    @Inject
    SecurityIdentity identity;

    @Inject
    UserService userService;

    @Inject
    AuditLogService auditLogService;

    @GET
    @Path("/policies")
    public Response listPolicies() {
        List<List<String>> policies = casbinService.listPolicies();
        return Response.ok(ApiResponse.ok(policies)).build();
    }

    @POST
    @Path("/policies")
    public Response addPolicy(Map<String, String> body, @HeaderParam("X-Forwarded-For") String xff, @HeaderParam("User-Agent") String ua) {
        String subject = body.get("subject");
        String object = body.get("object");
        String action = body.get("action");
        String username = identity.getPrincipal().getName();
        User u = userService.findByUsername(username);
        Long userId = u != null ? u.id : null;
        boolean added = casbinService.addPolicy(subject, object, action, userId, xff, ua);
        return Response.ok(ApiResponse.ok(Map.of("added", added))).build();
    }

    @DELETE
    @Path("/policies")
    public Response removePolicy(@QueryParam("subject") String subject,
                                 @QueryParam("object") String object,
                                 @QueryParam("action") String action,
                                 @HeaderParam("X-Forwarded-For") String xff,
                                 @HeaderParam("User-Agent") String ua) {
        String username = identity.getPrincipal().getName();
        User u = userService.findByUsername(username);
        Long userId = u != null ? u.id : null;
        boolean removed = casbinService.removePolicy(subject, object, action, userId, xff, ua);
        return Response.ok(ApiResponse.ok(Map.of("removed", removed))).build();
    }

    @POST
    @Path("/reload")
    public Response reload() {
        casbinService.reload();
        return Response.ok(ApiResponse.ok()).build();
    }
}
