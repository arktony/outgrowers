package com.farm_erp.outgrowers.controllers.endpoints;

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

@Path("business/outgrowers/permits")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Outgrowers (Permit)", description = "Manage permits")
@SecurityRequirement(name = "Authorization")
public class PermitController {
    @Inject
    PermitService service;

    @POST
    @Transactional
    @Operation(summary = "Generate permits", description = "This will create a permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response create(PermitGene request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.CREATE);
        if (session.status) {
            service.generate(request, session.user);
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Operation(summary = "Issue permits", description = "This will issue a permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response issue(List<Long> ids, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.CREATE);
        if (session.status) {
            service.issue(ids, session.user);
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label)).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("end/{id}")
    @Operation(summary = "End permit", description = "This will end a permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.END);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.endPermit(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("extend/{id}")
    @Operation(summary = "Extend permit", description = "This will extend a permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response extend(@PathParam("id") Long id, PermitRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.EXTEND);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.extendPermit(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Transactional
    @Operation(summary = "Get permits", description = "This will get permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(
            @QueryParam("status") String status,
            @QueryParam("isIssued") Boolean isIssued,
            @QueryParam("isExtended") Boolean isExtended,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.fetchPermits(status, isIssued, isExtended))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("{id}")
    @Transactional
    @Operation(summary = "Get permit details", description = "This will get permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Permit.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getDetails(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getDetails(id))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("batches")
    @Transactional
    @Operation(summary = "Get permit batches", description = "This will get permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PermitBatch.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getBatches(
            @NotNull @QueryParam("startDate") LocalDate startDate,
            @NotNull @QueryParam("endDate") LocalDate endDate,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.fetchPermitBatches(startDate, endDate))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("deliverynotes/{id}")
    @Transactional
    @Operation(summary = "Get permit delivery notes", description = "This will get permit delivery notes")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PermitBatch.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getDeliveryNotes(
            @PathParam("id") Long id,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getDeliveryNotes(id))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("deliverynotes")
    @Transactional
    @Operation(summary = "Get delivery notes", description = "This will get delivery notes")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PermitBatch.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getDeliveryNotess(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.OUTGROWER_REPORTS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.getDeliveryNotesBulk(startDate, endDate))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("deliverynotes/pdf/{id}")
    @Transactional
    @Operation(summary = "Get permit delivery notes pdf", description = "This will get permit delivery notes pdf")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Base64DataResponse.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getDeliveryNotesPDF(
            @PathParam("id") Long id,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.generateDeliveryNotPdf(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @GET
    @Path("pdf/{id}")
    @Transactional
    @Operation(summary = "Get permit PDF", description = "This will get permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Base64DataResponse.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getPDF(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.generatePdf(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }


    @GET
    @Path("blocks-for-generating")
    @Transactional
    @Operation(summary = "Get blocks to generate permits for", description = "This will get permit")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Base64DataResponse.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response getBlocks(
            @NotNull @QueryParam("startDate") LocalDate startDate,
            @NotNull @QueryParam("endDate") LocalDate endDate,
            @QueryParam("varietyId") Long varietyId,
            @QueryParam("villageId") Long villageId,
            @QueryParam("cropTypeId") Long cropTypeId,
            @QueryParam("districtOfficeId") Long districtOfficeId,
            @QueryParam("startAgeInMonths") Integer startAgeInMonths,
            @QueryParam("isAided") Boolean isAided,
            @QueryParam("plantPercentage") Double plantPercentage,
            @QueryParam("ratoonOnePercentage") Double ratoonOnePercentage,
            @QueryParam("ratoonTwoPercentage") Double ratoonTwoPercentage,
            @QueryParam("ratoonThreePercentage") Double ratoonThreePercentage,
            @QueryParam("totalExpectedTonnage") Double totalExpectedTonnage,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.OUTGROWER_MANAGEMENT, CategoryItemEnum.PERMIT,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.expectedHarvest(startDate, endDate, varietyId, villageId, cropTypeId, districtOfficeId,
                            startAgeInMonths, isAided, plantPercentage, ratoonOnePercentage, ratoonTwoPercentage,
                            ratoonThreePercentage, totalExpectedTonnage))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

