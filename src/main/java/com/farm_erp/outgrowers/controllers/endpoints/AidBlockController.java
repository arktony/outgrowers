package com.farm_erp.outgrowers.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.AidBlockService;
import com.farm_erp.outgrowers.controllers.services.BlockService;
import com.farm_erp.outgrowers.controllers.services.payloads.AidBlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockAidddRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockVerifyRequest;
import com.farm_erp.outgrowers.domains.AidBlock;
import com.farm_erp.outgrowers.domains.Block;
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
import java.util.List;

@Path("business/outgrowers/aid-blocks")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Aid Blocks)", description = "Manage aid blocks")
@SecurityRequirement(name = "Authorization")
public class AidBlockController {
    @Inject
    AidBlockService service;

    @POST
    @Path("{blockRatoonId}")
    @Transactional
    @Operation(summary = "Save aid blocks", description = "This will create new aid blocks")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = AidBlock.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(@PathParam("blockRatoonId") Long blockRatoonId, BlockAidddRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.AID_BLOCK,
                Privilege.CREATE);
        if (session.status) {
            service.create(blockRatoonId, request, session.user);
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Update the aid blocks", description = "This will update the aid blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = AidBlock.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, AidBlockRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.AID_BLOCK,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }


    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the aid blocks", description = "This will delete the aid blocks.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = AidBlock.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.AID_BLOCK,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

