package com.farm_erp.auth.controllers.endpoints;

import java.security.Principal;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.farm_erp.auth.controllers.services.UserAccountService;
import com.farm_erp.auth.controllers.services.models.PasswordUpdateRequest;
import com.farm_erp.auth.domain.User;
import com.farm_erp.configurations.handler.ResponseMessage;
import com.farm_erp.fileresources.controllers.services.FileRequest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("account-user")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Setting - User Account", description = "")
@SecurityRequirement(name = "Authorization")
public class UserAccountController {

	@Inject
	UserAccountService service;

	@GET
	@Operation(summary = "Get account details", description = "This will get details of logged in account")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
	public Response get(@Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		Optional<User> entity = User.findByEmail(user);

		return Response.ok(entity.get()).build();

	}

	@PUT
	@Path("password-update")
	@Transactional
	@Operation(summary = "Update account password", description = "This will update user account password to their personal preferred one.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
	public Response reset(PasswordUpdateRequest request, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		String response = service.updatePasword(user, request);
		return Response.ok(new ResponseMessage(response)).status(201).build();
	}

	@PUT
	@Path("profile-picture")
	@Transactional
	@Operation(summary = "Update profile picture", description = "This will users update account profile picture")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
	public Response updateProfilePic(FileRequest file, @Context SecurityContext ctx) {
		Principal caller = ctx.getUserPrincipal();
		String user = caller == null ? "anonymous" : caller.getName();

		String response = service.updateProfilePic(user, file);
		return Response.ok(new ResponseMessage(response)).status(201).build();
	}

}
