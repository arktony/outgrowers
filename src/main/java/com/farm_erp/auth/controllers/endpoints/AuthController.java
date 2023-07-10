package com.farm_erp.auth.controllers.endpoints;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.farm_erp.auth.controllers.services.AuthService;
import com.farm_erp.auth.controllers.services.models.LoginRequest;
import com.farm_erp.auth.controllers.services.models.LoginResponse;
import com.farm_erp.auth.controllers.services.models.URLUpdatePasswordRequest;
import com.farm_erp.configurations.handler.ResponseMessage;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("auth")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Auth - Authorization", description = "")
public class AuthController {

	@Inject
	AuthService service;

	@POST
	@Path("login")
	@Transactional
	@Operation(summary = "Authorize login using user name and password", description = "This will authorize user using username (not email) and password.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
	public Response login(LoginRequest request) {

		LoginResponse loginInfo = service.login(request);

		return Response.ok(loginInfo).status(200).build();
	}

	@PUT
	@Path("/forgot-password/{username}")
	@Transactional
	@Operation(summary = "Request password reset via user name", description = "This will initiate password reset via user name and generate reset password token sent to user's email.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
	public Response forgotPassword(@PathParam("username") String username) {

		String response = service.resetPasswordToken(username);

		return Response.ok(new ResponseMessage(response)).build();
	}

	@POST
	@Path("/password/{url}")
	@Transactional
	@Operation(summary = "Reset password via valid url", description = "This will reset password via valid url from email.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
	public Response updatePassword(@PathParam String url, URLUpdatePasswordRequest request) {

		String response = service.resetPasswordByUrl(url, request.password);

		return Response.ok(new ResponseMessage(response)).build();
	}

	@PUT
	@Path("/reset-password/{username}")
	@Transactional
	@Operation(summary = "Reset password using user name", description = "This will reset password via user name.")
	@APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
	public Response resetPassword(@PathParam("username") String username) {

		String response = service.resetPassword(username);

		return Response.ok(new ResponseMessage(response)).build();
	}

}
