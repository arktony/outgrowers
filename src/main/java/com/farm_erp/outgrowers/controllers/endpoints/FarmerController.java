package com.farm_erp.outgrowers.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.FarmerService;
import com.farm_erp.outgrowers.controllers.services.payloads.FarmerRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.FarmerUpdateRequest;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.outgrowers.domains.FarmerSingle;
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

@Path("business/outgrowers/farmers")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Farmers)", description = "Manage farmers")
@SecurityRequirement(name = "Authorization")
public class FarmerController {
    @Inject
    FarmerService service;

    @POST
    @Transactional
    @Operation(summary = "Save farmers", description = "This will create new farmers")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(FarmerRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
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
    @Operation(summary = "Update the farmers", description = "This will update the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, FarmerUpdateRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("verify/{id}")
    @Operation(summary = "Verify the farmers", description = "This will verify the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response verify(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.VERIFY);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.VERIFIED.label,
                    service.verify(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("activate/{id}")
    @Operation(summary = "Verify the farmers", description = "This will verify the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response activate(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.ACTIVATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.ACTIVATED.label,
                    service.activate(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("suspend/{id}")
    @Operation(summary = "Suspend the farmers", description = "This will suspend the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response suspend(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.SUSPEND);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SUSPENDED.label,
                    service.suspend(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("reinstate/{id}")
    @Operation(summary = "Reinstate the farmers", description = "This will reinstate the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response reinstate(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.REINSTATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.REINSTATED.label,
                    service.reinstate(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get farmers", description = "This will get the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.get())).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("{id}")
    @Transactional
    @Operation(summary = "Get farmer details", description = "This will get the farmer details.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = FarmerSingle.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getSingle(@PathParam("id") Long id,@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getSingle(id))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the farmers", description = "This will delete the farmers.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Farmer.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.FARMERS,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

