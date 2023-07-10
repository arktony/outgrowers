package com.farm_erp.auth.controllers.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.auth.controllers.services.RoleService;
import com.farm_erp.auth.controllers.services.models.RoleRequest;
import com.farm_erp.auth.controllers.services.models.RoleSummaryResponse;
import com.farm_erp.auth.controllers.services.models.UserRequest;
import com.farm_erp.auth.domain.Role;
import com.farm_erp.auth.domain.User;
import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.auth.validators._SessionStatus;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.configurations.handler._Messages_Enum;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("role")
@Produces("application/json")
@Consumes("application/json")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Auth - Role", description = "")
public class RoleController {

	@Inject
	RoleService service;

	@POST
	@Transactional
	@Operation(summary = "Create role", description = "This will create a role")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))
	public Response create(RoleRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.CREATE);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.create(session.user, request)))
					.status(200).build();
		}
		throw new WebApplicationException(session.response, 401);
	}

	@GET
	@Transactional
	@Operation(summary = "Get roles", description = "This will get a list of roles")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = Role.class)))
	public Response getList(@Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.VIEW);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.get())).status(200).build();
		}
		throw new WebApplicationException(session.response, 401);
	}

	@GET
	@Path("summary")
	@Transactional
	@Operation(summary = "Get roles summary", description = "This will get a summarised list of roles")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = RoleSummaryResponse.class)))
	public Response getSummaryList(@Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.VIEW);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.get())).status(200).build();
		}

		throw new WebApplicationException(session.response, 401);

	}

	@GET
	@Path("{id}")
	@Transactional
	@Operation(summary = "Get details", description = "This will get a role detail.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))
	public Response getDetails(@PathParam("id") Long id, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.VIEW);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.getSingle(id))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);

	}

	@PUT
	@Path("{id}")
	@Transactional
	@Operation(summary = "Update role", description = "This will update role details.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response update(@PathParam("id") Long id, RoleRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.UPDATED.label, service.update(id, request)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("deactivate/{id}")
	@Transactional
	@Operation(summary = "Deactivate role", description = "This will deactivate user role.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))
	public Response deactivate(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.DEACTIVATED.label, service.deactivate(id))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("activate/{id}")
	@Transactional
	@Operation(summary = "Activate role", description = "This will activate user role.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))
	public Response activate(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.ACTIVATED.label, service.activate(id))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@DELETE
	@Path("{id}")
	@Transactional
	@Operation(summary = "Delete role", description = "This will delete user role.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))
	public Response delete(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user, CategoryEnum.SETTINGS, CategoryItemEnum.ROLES,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label, service.delete(id))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);
	}

}
