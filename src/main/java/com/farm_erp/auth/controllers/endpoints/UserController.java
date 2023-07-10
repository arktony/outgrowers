package com.farm_erp.auth.controllers.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.auth.controllers.services.UserService;
import com.farm_erp.auth.controllers.services.models.UserRequest;
import com.farm_erp.auth.controllers.services.models.UserSummaryResponse;
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

@Path("user")
@Produces("application/json")
@Consumes("application/json")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Auth - User", description = "")
public class UserController {

	@Inject
	UserService service;

	@POST
	@Transactional
	@Operation(summary = "Create User", description = "This will create a user")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response create(UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.CREATE);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.create(session.user, request)))
					.status(200).build();
		}
		throw new WebApplicationException(session.response, 401);
	}

	@GET
	@Transactional
	@Operation(summary = "Get users", description = "This will get a list of users")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = User.class)))
	public Response getList(@Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.VIEW);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.get())).status(200).build();
		}
		throw new WebApplicationException(session.response, 401);
	}

	@GET
	@Path("summary")
	@Transactional
	@Operation(summary = "Get users' summary", description = "This will get a summarized list of users")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = UserSummaryResponse.class)))
	public Response getSummaryList(@Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.VIEW);
		if (session.status) {
			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.getSummary())).status(200)
					.build();
		}
		throw new WebApplicationException(session.response, 401);
	}

	@GET
	@Path("{id}")
	@Transactional
	@Operation(summary = "Get details", description = "This will return user details.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response getDetails(@PathParam("id") Long id, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.VIEW);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.getSingle(id))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);

	}

	@PUT
	@Path("resend-credentials/{id}")
	@Transactional
	@Operation(summary = "Resend Credentials", description = "This will update user password and send an email of the new credentials.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response resendCredentials(@PathParam("id") Long id, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.VIEW);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.SUCCESS.label, service.resendCreds(id, session.user))).status(200)
					.build();
		}

		throw new WebApplicationException(session.response, 401);

	}

	@PUT
	@Path("{id}")
	@Transactional
	@Operation(summary = "Update user", description = "This will update user details.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response update(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(
					new ResponseMessage(_Messages_Enum.UPDATED.label, service.updateUser(session.user, id, request)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("activate/{id}")
	@Transactional
	@Operation(summary = "Activate user", description = "This will activate a user.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response activate(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.ACTIVATED.label, service.activateUser(id)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@PUT
	@Path("deactivate/{id}")
	@Transactional
	@Operation(summary = "Deactivate user", description = "This will deactivate a user.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response deactivate(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.DEACTIVATED.label, service.deactivateUser(id)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

	@DELETE
	@Path("{id}")
	@Transactional
	@Operation(summary = "Delete user", description = "This will delete a user.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response delete(@PathParam("id") Long id, UserRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		_SessionStatus session = SessionValidator.validate(user,  CategoryEnum.SETTINGS, CategoryItemEnum.USERS,
				Privilege.UPDATE);
		if (session.status) {

			return Response.ok(new ResponseMessage(_Messages_Enum.DELETED.label, service.delete(id)))
					.status(200).build();
		}

		throw new WebApplicationException(session.response, 401);
	}

}
