package com.farm_erp.settings.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.Base64DataResponse;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.PermitService;
import com.farm_erp.outgrowers.controllers.services.payloads.PermitGene;
import com.farm_erp.outgrowers.controllers.services.payloads.PermitRequest;
import com.farm_erp.outgrowers.domains.Permit;
import com.farm_erp.outgrowers.domains.PermitBatch;
import com.farm_erp.settings.controllers.services.CanePriceService;
import com.farm_erp.settings.domains.CanePrice;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Path("business/settings/caneprices")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Cane Prices", description = "Manage cane prices")
@SecurityRequirement(name = "Authorization")
public class CanePriceController {
    @Inject
    CanePriceService service;


    @GET
    @Transactional
    @Operation(summary = "Get cane prices", description = "This will get cane prices")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = CanePrice.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(
            @NotNull @QueryParam("startDate") LocalDate startDate,
            @NotNull @QueryParam("endDate") LocalDate endDate,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.OUTGROWER_REPORTS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.fetch(startDate, endDate))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}

