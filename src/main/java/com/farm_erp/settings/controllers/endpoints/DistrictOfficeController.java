package com.farm_erp.settings.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.settings.controllers.services.DistrictOfficeService;
import com.farm_erp.settings.controllers.services.payloads.DistrictOfficeRequest;
import com.farm_erp.settings.domains.District;
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

@Path("business/settings/district-offices")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Settings (District Office)", description = "Manage district offices")
@SecurityRequirement(name = "Authorization")
public class DistrictOfficeController {
    @Inject
    DistrictOfficeService service;

    @POST
    @Transactional
    @Operation(summary = "Save sub-district", description = "This will create new  sub-district")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = District.class)))
    public Response create(DistrictOfficeRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.DISTRICT_OFFICES,
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
    @Operation(summary = "Update the  sub-district", description = "This will update the  sub-district.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = District.class)))
    public Response update(@PathParam("id") Long id, DistrictOfficeRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.DISTRICT_OFFICES,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get  sub-district", description = "This will get the  sub-district.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = EnumList.class)))
    public Response get(@QueryParam("zoneId") Long zoneId,@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.DISTRICT_OFFICES,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.get(zoneId))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the  sub-district", description = "This will delete the  sub-district.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = District.class)))
    public Response delete(@PathParam("id") Long id, DistrictOfficeRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.DISTRICT_OFFICES,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

