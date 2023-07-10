package com.farm_erp.outgrowers.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.BlockCropTypeService;
import com.farm_erp.outgrowers.controllers.services.BlockService;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockCropTypeRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockVerifyRequest;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;
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

@Path("business/outgrowers/block-ratoon")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Block Ratoon)", description = "Manage block ratoons")
@SecurityRequirement(name = "Authorization")
public class BlockCropTypeController {
    @Inject
    BlockCropTypeService service;

    @POST
    @Transactional
    @Operation(summary = "Save block ratoon", description = "This will create new block ratoon")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BlockCropType.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(BlockCropTypeRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.BLOCK_CYCLE,
                Privilege.CREATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label, service.create(request, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}

