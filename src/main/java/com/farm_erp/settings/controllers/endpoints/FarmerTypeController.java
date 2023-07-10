package com.farm_erp.settings.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.settings.controllers.services.FarmerTypeService;
import com.farm_erp.settings.controllers.services.payloads.FarmerTypeRequest;
import com.farm_erp.settings.domains.FarmerType;
import com.farm_erp.statics.EnumList;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("business/settings/farmer-type")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Settings (Farmer types)", description = "Manage  farmer type")
@SecurityRequirement(name = "Authorization")
public class FarmerTypeController {
    @Inject
    FarmerTypeService service;

    @POST
    @Transactional
    @Operation(summary = "Save farmer type", description = "This will create new  farmer type")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = FarmerType.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(FarmerTypeRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label, service.create(request, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Update the  farmer type", description = "This will update the  farmer type.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = FarmerType.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, FarmerTypeRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get  farmer type", description = "This will get the  farmer type.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = EnumList.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.get())).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the  farmer type", description = "This will delete the  farmer type.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = FarmerType.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateOpen(user);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

