package com.farm_erp.accounting.controllers.endpoints;

import com.farm_erp.accounting.controllers.services.FarmerMoneyService;
import com.farm_erp.accounting.domains.FarmerMoneyActivity;
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
import java.time.LocalDate;
import java.util.List;

@Path("business/farmer/statement")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Accounting (Farmer Money Activity)", description = "Manage  farmer money activities")
@SecurityRequirement(name = "Authorization")
public class FarmerMoneyController {
    @Inject
    FarmerMoneyService service;

    @GET
    @Transactional
    @Path("{farmerId}")
    @Operation(summary = "Get payments for single farmer", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = FarmerMoneyActivity.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("farmerId") Long farmerId,
                           @QueryParam("start") LocalDate start,
                           @QueryParam("end") LocalDate end,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getByFarmer(farmerId, start, end))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}

