package com.farm_erp.weigh_bridge.controllers.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.weigh_bridge.controllers.services.payloads.PermitData;
import com.farm_erp.weigh_bridge.controllers.services.payloads.SingleRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.outgrowers.domains.Permit;
import com.farm_erp.weigh_bridge.controllers.services.WeighBridgeService;
import com.farm_erp.weigh_bridge.controllers.services.payloads.LoadedRequest;
import com.farm_erp.weigh_bridge.controllers.services.payloads.UnLoadedRequest;
import com.farm_erp.weigh_bridge.domains.WeighBridgeTicket;

@Path("business/weighbridge")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Weigh Bridge ", description = "Manage permits")
@SecurityRequirement(name = "Authorization")
public class WeighBridgeController {

    @Inject
    WeighBridgeService service;

    @GET
    @Path("{serialNumber}")
    @Transactional
    @Operation(summary = "Get permit details", description = "This will get permit details")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PermitData.class)))
    @APIResponse(description = "Invalid  setting selected", responseCode = "404")
    public Response get(@PathParam("serialNumber") String serialNumber, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateApplication(user);
        if (session.status) {
            return Response.ok(new ResponseMessage(_Messages_Enum.FETCHED.label, service.getDetails(serialNumber)))
                    .build();
        }
        throw new WebApplicationException(session.response, 401);
    }

//    @PUT
//    @Path("capture-gross-weight/{serialNumber}")
//    @Transactional
//    @Operation(summary = "Capture the gross wight", description = "This will capture the gross weight")
//    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = WeighBridgeTicket.class)))
//    @APIResponse(description = "Setting already exists", responseCode = "409")
//    public Response captureWeight(@Context SecurityContext ctx, @PathParam("serialNumber") String serialNumber,
//                                  LoadedRequest request) {
//        Principal caller = ctx.getUserPrincipal();
//        String user = caller == null ? "anonymous" : caller.getName();
//
//        _SessionStatus session = SessionValidator.validateApplication(user);
//        if (session.status) {
//            service.captureWeight(serialNumber, request, session.accessKey);
//            return Response.ok(new ResponseMessage("Gross weight captured successfully")).build();
//        }
//        throw new WebApplicationException(session.response, 401);
//    }
//
//    @PUT
//    @Path("capture-tare-weight/{serialNumber}")
//    @Transactional
//    @Operation(summary = "Capture the tare weight", description = "This will capture the tare weight")
//    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = WeighBridgeTicket.class)))
//    @APIResponse(description = "Setting already exists", responseCode = "409")
//    public Response captureTareWeight(@Context SecurityContext ctx, @PathParam("serialNumber") String serialNumber,
//                                      UnLoadedRequest request) {
//        Principal caller = ctx.getUserPrincipal();
//        String user = caller == null ? "anonymous" : caller.getName();
//
//        _SessionStatus session = SessionValidator.validateApplication(user);
//        if (session.status) {
//            service.captureTareWeight(serialNumber, request, session.accessKey);
//            return Response.ok(new ResponseMessage("Tare weight captured successfully")).build();
//        }
//        throw new WebApplicationException(session.response, 401);
//    }

    @PUT
    @Path("capture-weight")
    @Transactional
    @Operation(summary = "Capture the weight", description = "This will capture the weight")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = WeighBridgeTicket.class)))
    @APIResponse(description = "Setting already exists", responseCode = "409")
    public Response captureWeight(@Context SecurityContext ctx,
                                      SingleRequest request) {
        Principal caller = ctx.getUserPrincipal();
        String user = caller == null ? "anonymous" : caller.getName();

        _SessionStatus session = SessionValidator.validateApplication(user);
        if (session.status) {
            service.capture(request, session.accessKey);
            return Response.ok(new ResponseMessage("Weight captured successfully")).status(200).build();
        }
        throw new WebApplicationException(session.response, 401);
    }

}
