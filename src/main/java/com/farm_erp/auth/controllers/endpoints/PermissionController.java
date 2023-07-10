package com.farm_erp.auth.controllers.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.auth.controllers.services.PermissionService;
import com.farm_erp.auth.controllers.services.models.RoleRequest;
import com.farm_erp.auth.domain.RoleCategory;
import com.farm_erp.auth.domain.RoleCategoryItem;
import com.farm_erp.auth.domain.RoleCategoryPrivilege;
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
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("permissions")
@Produces("application/json")
@Consumes("application/json")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Auth - Permissions", description = "")
public class PermissionController {

	@Inject
	PermissionService service;

	@PUT
	@Path("privilege/{id}")
	@Transactional
	@Operation(summary = "Update Role category privilege", description = "")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = RoleCategoryPrivilege.class)))
	public Response update(@PathParam("id") Long id, RoleRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label, service.update(id, session.user)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("category/{id}")
	@Transactional
	@Operation(summary = "Update Role category", description = "")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = RoleCategory.class)))
	public Response updateRoleCategory(@PathParam("id") Long id, RoleRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label, service.updateRoleCategory(id, session.user)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("category-item/{id}")
	@Transactional
	@Operation(summary = "Update Role category item", description = "")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = RoleCategoryItem.class)))
	public Response updateRoleCategoryItem(@PathParam("id") Long id, RoleRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label, service.updateRoleCategoryItem(id, session.user)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

}
