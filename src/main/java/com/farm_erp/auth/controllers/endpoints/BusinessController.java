package com.farm_erp.auth.controllers.endpoints;

import com.farm_erp.auth.controllers.services.BusinessService;
import com.farm_erp.auth.controllers.services.models.BusinessRequest;
import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;
import com.farm_erp.fileresources.controllers.services.FileRequest;
import com.farm_erp.settings.domains.Business;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("business")
@Produces("application/json")
@Consumes("application/json")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Business - Business", description = "")
public class BusinessController {

	@Inject
	BusinessService service;

	@PUT
	@Path("{id}")
	@Transactional
	@Operation(summary = "Update user", description = "This will update user details.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Business.class)))
	public Response update(@PathParam("id") Long id, BusinessRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.BUSINESS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(
					new ResponseMessage(_Messages_Enum.UPDATED.label, service.update(id, request, session.user)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("logo/{id}")
	@Transactional
	@Operation(summary = "Update business logo", description = "")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Business.class)))
	public Response updateLogo(@PathParam("id") Long id, FileRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.BUSINESS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(
							new ResponseMessage(_Messages_Enum.UPDATED.label, service.updateLogo(id, request, session.user)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

}
