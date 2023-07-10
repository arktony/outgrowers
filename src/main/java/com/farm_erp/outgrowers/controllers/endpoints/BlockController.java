package com.farm_erp.outgrowers.controllers.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.BlockService;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockVerifyRequest;
import com.farm_erp.outgrowers.domains.Block;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("business/outgrowers/blocks")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Blocks)", description = "Manage blocks")
@SecurityRequirement(name = "Authorization")
public class BlockController {
    @Inject
    BlockService service;

    @POST
    @Transactional
    @Operation(summary = "Save blocks", description = "This will create new blocks")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(BlockRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
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
    @Operation(summary = "Update the blocks", description = "This will update the blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, BlockRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
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
    @Operation(summary = "Verify the blocks", description = "This will verify the blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response verify(@PathParam("id") Long id, BlockVerifyRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
                Privilege.VERIFY);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.verify(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }


    @GET
    @Transactional
    @Path("details/{id}")
    @Operation(summary = "Get block details", description = "This will fetch block details")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getDetail(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
                Privilege.VERIFY);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.getDetails(id))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get blocks", description = "This will get the blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@Context SecurityContext ctx, @QueryParam("districtId") Long districtId, @QueryParam("villageId") Long villageId) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.get(districtId, villageId))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the blocks", description = "This will delete the blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Block.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCKS,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

