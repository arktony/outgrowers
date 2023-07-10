package com.farm_erp.weigh_bridge.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.weigh_bridge.controllers.services.TransportFareService;
import com.farm_erp.weigh_bridge.controllers.services.payloads.TransportFaresRequest;
import com.farm_erp.weigh_bridge.domains.TransportFares;
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

@Path("business/weigh-bridge/transport-fares")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Weighing Bridge (TransportFares)", description = "Manage  transport fares")
@SecurityRequirement(name = "Authorization")
public class TransportFaresController {
    @Inject
    TransportFareService service;

    @POST
    @Transactional
    @Operation(summary = "Save transport fares", description = "This will create new  transport fares")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TransportFares.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(TransportFaresRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.WEIGHING_BRIDGE, CategoryItemEnum.VEHICLES,
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
    @Operation(summary = "Update the  transport fares", description = "This will update the  transport fares.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TransportFares.class)))
    @APIResponse(description = "Invalid  vehicle selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, TransportFaresRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.WEIGHING_BRIDGE, CategoryItemEnum.VEHICLES,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get  transport fares", description = "This will get the  transport fares.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TransportFares.class)))
    @APIResponse(description = "Invalid  vehicle selected", responseCode = "404")
    public Response get(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.WEIGHING_BRIDGE, CategoryItemEnum.VEHICLES,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.get())).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the  transport fares", description = "This will delete the  transport fares.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TransportFares.class)))
    @APIResponse(description = "Invalid  vehicle selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, TransportFaresRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.WEIGHING_BRIDGE, CategoryItemEnum.VEHICLES,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

