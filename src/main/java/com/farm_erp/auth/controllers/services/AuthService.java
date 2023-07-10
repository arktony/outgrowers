/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.auth.controllers.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.controllers.services.models.LoginRequest;
import com.farm_erp.auth.controllers.services.models.LoginResponse;
import com.farm_erp.auth.domain.User;
import com.farm_erp.auth.validators.SessionValidator;
import com.farm_erp.configurations.security.JwtUtils;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.utilities.AES;
import com.farm_erp.utilities.EmailServices;
import com.farm_erp.utilities.Lazy;
import com.farm_erp.utilities.RandomGenerator;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author aiden
 */
@ApplicationScoped
public class AuthService {

	@Inject
	JwtUtils jwtUtils;

	@Inject
	SessionValidator validator;

	@Inject
	FileService fileService;

	@Inject
	UserService userService;

	@Inject
	EmailServices emailServices;

	public LoginResponse login(LoginRequest request) {

		User user = User.login(request.email, request.password);
		if (user == null) {
			throw new WebApplicationException("Wrong credentials. Access denied.", 401);
		}
		
		if (user.status.equals(_StatusTypes_Enum.DEACTIVATED.toString())) {
			throw new WebApplicationException("Account has been de-activated!", 401);
		}

		String jwt = jwtUtils.generateJwtToken(user.email);

		LoginResponse loginInfo = new LoginResponse();
		loginInfo.user = user;
		loginInfo.jwt = jwt;

		return loginInfo;
	}

	public String resetPasswordToken(String username) {
		Optional<User> validUser = User.findByEmail(username);
		if (!validUser.isPresent()) {
			throw new WebApplicationException("User doesn't exist!", 404);
		}

		String token = UUID.randomUUID().toString();

		JsonObject tokenObj = new JsonObject();

		tokenObj.put("token", token);
		tokenObj.put("expiry", calculateExpiryDate(60 * 24).toInstant());
		tokenObj.put("user", username);

//		emailServices.createAndSendPasswordRecoveryEmail(validUser.email,
//				validUser.firstname + " " + validUser.lastname, tokenObj.toString());

		return "Please check email and click link to reset password!";
	}

	private Date calculateExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

	public String resetPasswordByUrl(String url, String password) {

		String decryptedtoken = AES.decrypt(Lazy.URLDecode(url));

		JsonObject valid = validateResetPsswrdToken(decryptedtoken);

		if (valid.getString("response").equals("Valid")) {
			String username = valid.getString("user");

			Optional<User> user = User.findByEmail(username);
			user.get().password = BcryptUtil.bcryptHash(password);
			user.get().persist();

			return "Password reset successfully!";

		}
		return valid.getString("response");
	}

	private static JsonObject validateResetPsswrdToken(String token) {

		JsonObject tokenObj = new JsonObject(token);
		JsonObject response = new JsonObject();

		System.out.println("Object " + token);
		System.out.println("Token " + tokenObj.getString("token"));
		System.out.println("Expiry " + tokenObj.getString("expiry"));

		if (tokenObj.getString("token") == null) {
			response.put("response", "Invalid token");
			return response;
		}

		String expiration = tokenObj.getString("expiry");

		if (expiration == null) {
			response.put("response", "Invalid token");
			return response;
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

		LocalDateTime expiryLocalDate = LocalDateTime.parse(expiration, dateFormatter);
		Date expiryTime = Date.from(expiryLocalDate.toInstant(ZoneOffset.UTC));

		final Calendar cal = Calendar.getInstance();

		if ((expiryTime.getTime() - cal.getTime().getTime()) <= 0) {
			response.put("response", "Token expired");
			return response;

		}

		response.put("response", "Valid");
		response.put("user", tokenObj.getString("user"));

		return response;
	}

	public String resetPassword(String email) {
		Optional<User> validUser = User.findByEmail(email);
		if (!validUser.isPresent()) {
			throw new WebApplicationException("User doesn't exist!", 404);
		}

		String genpassword = RandomGenerator.randomString(6);

		String password = BcryptUtil.bcryptHash(genpassword);

		validUser.get().password = password;

		System.out.println("..." + genpassword);

//		emailServices.createAndSendPasswordResetEmail(validUser.email, validUser.firstname + " " + validUser.lastname,
//				genpassword);

		return "Please check email and click link to reset password!";
	}

}
