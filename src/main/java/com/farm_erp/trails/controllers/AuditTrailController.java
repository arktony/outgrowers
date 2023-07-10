package com.farm_erp.trails.controllers;

import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

@Path("audit-trail")
@Produces("application/json")
@Consumes("application/json")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Audit Trail", description = "")
public class AuditTrailController {

	@Inject
	AuditTrailService auditTrailService;

	@GET
	@Path("{id}")
	public Response fetch(@PathParam("id") Long id, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validateOpen(user);
		if (session.status) {
			List<AuditTrail> entity = auditTrailService.get(id);
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, entity)).build();
		}

		throw new WebApplicationException("Not allowed", 401);
	}

}
