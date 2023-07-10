package com.farm_erp.settings.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.settings.controllers.services.GBSettingsService;
import com.farm_erp.settings.controllers.services.payloads.GeneralBusinessSettingsRequest;
import com.farm_erp.settings.controllers.services.payloads.GeneralBusinessSettingsUpdateRequest;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
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

@Path("business/settings/general-settings")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Settings (General Settings)", description = "Manage  settings")
@SecurityRequirement(name = "Authorization")
public class GBSettingsController {
    @Inject
    GBSettingsService service;

    @POST
    @Transactional
    @Operation(summary = "Save settings", description = "This will create new  settings")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = GeneralBusinessSettings.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(GeneralBusinessSettingsRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.SETTINGS,
                Privilege.CREATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label, service.create(request, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Update the  settings", description = "This will update the  settings.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = GeneralBusinessSettings.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, GeneralBusinessSettingsUpdateRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.SETTINGS,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get  settings", description = "This will get the  settings.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = GeneralBusinessSettings.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.SETTINGS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.getSettings())).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the  settings", description = "This will delete the  settings.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = GeneralBusinessSettings.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, GeneralBusinessSettingsRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.SETTINGS,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

