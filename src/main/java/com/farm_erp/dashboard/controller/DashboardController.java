package com.farm_erp.dashboard.controller;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.dashboard.service.DashboardService;
import com.farm_erp.dashboard.service.payloads.SalesResponse;
import com.farm_erp.dashboard.service.payloads.SingleCardValue;
import com.farm_erp.statics.Dashboard_Graph_Enum;
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

@Path("dashboard")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Dashboard", description = "Fetch dashboard data")
@SecurityRequirement(name = "Authorization")
public class DashboardController {

    @Inject
    DashboardService service;

    @GET
    @Path("cards")
    @Transactional
    @Operation(summary = "Card statistics", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SingleCardValue.class)))
    public Response card(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.DASHBOARD, CategoryItemEnum.DASHBOARD,
                Privilege.VIEW);
        if (Boolean.TRUE.equals(session.status)) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label,
                    service.singleCard(session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("harvest/{periodEnum}")
    @Transactional
    @Operation(summary = "Attendance trend", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SalesResponse.class)))
    public Response salesTrend(@PathParam("periodEnum") Dashboard_Graph_Enum periodEnum,
                               @QueryParam("startDate") LocalDate startDate, @QueryParam("endDate") LocalDate endDate, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.DASHBOARD, CategoryItemEnum.DASHBOARD,
                Privilege.VIEW);
        if (Boolean.TRUE.equals(session.status)) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label,
                            service.haarvests(periodEnum, startDate, endDate, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("blocks/{periodEnum}")
    @Transactional
    @Operation(summary = "Employee hire trend", description = "")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = SalesResponse.class)))
    public Response markedVolume(@PathParam("periodEnum") Dashboard_Graph_Enum periodEnum,
                                 @QueryParam("startDate") LocalDate startDate, @QueryParam("endDate") LocalDate endDate,
                                 @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.DASHBOARD, CategoryItemEnum.DASHBOARD,
                Privilege.VIEW);
        if (Boolean.TRUE.equals(session.status)) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label,
                            service.blocks(periodEnum, startDate, endDate, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}
