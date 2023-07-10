package com.farm_erp.accounting.controllers.endpoints;

import com.farm_erp.accounting.controllers.services.AidPaymentService;
import com.farm_erp.accounting.domains.AidPaymentActivity;
import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
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

@Path("business/accounting/aid-payments")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Accounting (Aid Payments)", description = "Manage  aid payments")
@SecurityRequirement(name = "Authorization")
public class AidPaymentController {
    @Inject
    AidPaymentService service;

    @GET
    @Transactional
    @Path("block/{id}")
    @Operation(summary = "Get payments for single block", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = AidPaymentActivity.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getByBlock(id))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}

