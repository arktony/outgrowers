package com.farm_erp.outgrowers.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.controllers.services.OutgrowerReportService;
import com.farm_erp.outgrowers.domains.BlockAidData;
import com.farm_erp.outgrowers.domains.ExpectedHarvest;
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
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

@Path("business/outgrowers/reports")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Reports)", description = "Manage reports")
@SecurityRequirement(name = "Authorization")
public class OutGrowerReportController {
    @Inject
    OutgrowerReportService service;

    @GET
    @Path("expected-harvest")
    @Transactional
    @Operation(summary = "Get expected harvest", description = "This will get expected harvest")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ExpectedHarvest.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@NotNull @QueryParam("startDate") LocalDate startDate, @NotNull @QueryParam("endDate") LocalDate endDate,
                        @QueryParam("varietyId") Long varietyId, @QueryParam("cropTypeId") Long cropTypeId, @QueryParam("startAgeInMonths") Integer startAgeInMonths,
                        @Context SecurityContext ctx) throws IOException {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.OUTGROWER_REPORTS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.expectedHarverst(startDate, endDate, varietyId, cropTypeId, startAgeInMonths))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("aided-blocks")
    @Transactional
    @Operation(summary = "Get aided blocks", description = "This will get aided blocks")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BlockAidData.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getAided(@Context SecurityContext ctx) throws IOException {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.OUTGROWER_REPORTS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.aidedBlocks())).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

