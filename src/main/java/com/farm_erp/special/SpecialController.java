package com.farm_erp.special;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.domains.Block;

@Path("special")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Special", description = "Manage blocks")
@SecurityRequirement(name = "Authorization")
public class SpecialController {
    @Inject
    SpecialService service;

    @PUT
    @Transactional
    @Operation(summary = "Rectify farmer codes", description = ".")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (Boolean.TRUE.equals(session.status)) {
            service.rectifyCodes();
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Path("codes")
    @Transactional
    @Operation(summary = "Rectify farmer codes", description = ".")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response updateCodes(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (Boolean.TRUE.equals(session.status)) {
            service.rectifyBlockCodes();
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Path("village_codes")
    @Transactional
    @Operation(summary = "Rectify village codes", description = ".")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response updateVillageCodes(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (Boolean.TRUE.equals(session.status)) {
            service.rectifyVillageCodes();
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete farmer", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (Boolean.TRUE.equals(session.status)) {
            service.delete(id);
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

