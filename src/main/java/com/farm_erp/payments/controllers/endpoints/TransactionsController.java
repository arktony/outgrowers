package com.farm_erp.payments.controllers.endpoints;

import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.payments.controllers.services.TransactionService;
import com.farm_erp.payments.controllers.services.payloads.TransactionRequest;
import com.farm_erp.payments.domains.Transaction;
import com.farm_erp.payments.domains.Transaction;
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

@Path("business/payments/transactions")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Payments (Transactions)", description = "Manage transactions")
@SecurityRequirement(name = "Authorization")
public class TransactionsController {
    @Inject
    TransactionService service;

    @POST
    @Transactional
    @Operation(summary = "Save transactions", description = "This will create new transactions")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response create(TransactionRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.CREATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.SAVED.label, service.create(request, session.user)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("{id}")
    @Operation(summary = "Update the transactions", description = "This will update the transactions.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response update(@PathParam("id") Long id, TransactionRequest request,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.UPDATE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label,
                    service.update(id, request, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("approve/{id}")
    @Operation(summary = "Approve the transactions", description = "This will approve the transactions.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response approve(@PathParam("id") Long id,
                            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.APPROVE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.APPROVED.label,
                    service.approve(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @PUT
    @Transactional
    @Path("reject/{id}")
    @Operation(summary = "Reject the transactions", description = "This will reject the transactions.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response reject(@PathParam("id") Long id,
                           @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.REJECT);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.REJECTED.label,
                    service.reject(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }


    @GET
    @Transactional
    @Operation(summary = "Get transactions", description = "This will get the transactions.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(
            @QueryParam("start") LocalDate start,
            @QueryParam("end") LocalDate end,
            @QueryParam("status") String status,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.VIEW);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label,
                    service.get(start, end, status))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Delete the transactions", description = "This will delete the transactions.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validate(user, CategoryEnum.PAYMENTS, CategoryItemEnum.TRANSACTIONS,
                Privilege.DELETE);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label,
                    service.delete(id, session.user))).build();
        }
        throw new WebApplicationException(session.response, 401);
    }
}

