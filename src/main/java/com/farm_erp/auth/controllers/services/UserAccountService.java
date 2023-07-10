package com.farm_erp.auth.controllers.services;

import com.farm_erp.auth.controllers.services.models.PasswordUpdateRequest;
import com.farm_erp.auth.domain.User;
import com.farm_erp.fileresources.controllers.services.FileRequest;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.fileresources.domain.FileResource;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class UserAccountService {

	@Inject
	FileService fileService;

	public String updatePasword(String username, PasswordUpdateRequest request) {
		Optional<User> entity = User.findByEmail(username);
		if (!entity.isPresent()) {
			throw new WebApplicationException("Invalid user!", 404);
		}

		if (request.newPassword.length() > 3
				&& request.newPassword.equals(request.confirmPassword)
				&& !request.newPassword.equals(request.oldPassword)) {

			if (User.verifyPassword(request.oldPassword, entity.get().password)) {
				User.updatePassword(entity.get(), request.newPassword);

			} else {
				return "Invalid password!";
			}

		} else {
			return "Invalid Password!";
		}

		return "Password updated successfully!";
	}

	public String updateProfilePic(String username, FileRequest file) {

		Optional<User> entity = User.findByEmail(username);
		if (!entity.isPresent()) {
			throw new WebApplicationException("Invalid user!", 404);
		}

		if (entity.get().photo == null) {
			FileResource newFile = fileService.create(file);
			entity.get().photo = newFile;
		} else {
			FileResource newFile = fileService.update(entity.get().photo.id, file);
			entity.get().photo = newFile;
		}

		entity.get().persist();

		return "User profile picture updated successfully!";
	}

}
