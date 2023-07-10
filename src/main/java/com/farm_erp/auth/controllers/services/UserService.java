package com.farm_erp.auth.controllers.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.controllers.services.models.UserRequest;
import com.farm_erp.auth.controllers.services.models.UserSummaryResponse;
import com.farm_erp.auth.domain.Role;
import com.farm_erp.auth.domain.User;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.fileresources.domain.FileResource;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.utilities.EmailServices;
import com.farm_erp.utilities.RandomGenerator;

import io.agroal.api.AgroalDataSource;
import io.quarkus.elytron.security.common.BcryptUtil;

@ApplicationScoped
public class UserService {

	@Inject
	FileService fileService;

	@Inject
	EmailServices emailServices;

	@Inject
	AgroalDataSource dataSource;

	public User create(User user, UserRequest request) {

		Role role = Role.findById(request.roleId);
		if (role == null) {
			throw new WebApplicationException("Untracable roles included.", 422);
		}
		if (!role.isApproved) {
			throw new WebApplicationException("Unapproved roles included.", 422);
		}

		String genpassword = RandomGenerator.randomString(6);

		String password = BcryptUtil.bcryptHash(genpassword);

		User newuser = new User();
		newuser.email = request.email;
		newuser.password = password;
		newuser.firstname = request.firstname;
		newuser.lastname = request.lastname;
		newuser.othername = request.othername;
		newuser.phone = request.phone;
		newuser.role = role;
		newuser.business = user.business;
		
		// save photo if provided
		if (request.photo != null) {
			FileResource newFile = fileService.create(request.photo);
			newuser.photo = newFile;
		}

		newuser.persist();

		emailServices.createAndSendCredentialsEmail(request.firstname + " " + request.lastname, newuser.email,
				genpassword, user.business);

		return newuser;
	}

	public User resendCreds(Long userId, User user) {

		User toresendUser = User.findById(userId);
		if (toresendUser == null) {
			throw new WebApplicationException("User not found.", 404);
		}

		String genpassword = RandomGenerator.randomString(6);

		String password = BcryptUtil.bcryptHash(genpassword);

		toresendUser.password = password;
		toresendUser.persist();

		emailServices.createAndSendCredentialsEmail(toresendUser.firstname + " " + toresendUser.lastname,
				toresendUser.email, genpassword, user.business);

		return toresendUser;
	}

	public List<User> get() {

		return User.listAll();

	}

	public List<UserSummaryResponse> getSummary() {

		List<UserSummaryResponse> response = new ArrayList<>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {

			connection = dataSource.getConnection();
			if (connection != null) {

				statement = connection.prepareStatement("select "
						+ "id, "
						+ "concat(firstname,' ',lastname) as name "
						+ "from User "
						+ "where status = '"
						+ _StatusTypes_Enum.ACTIVE.toString()
						+ "'");

				resultSet = statement.executeQuery();
				while (resultSet.next()) {
					UserSummaryResponse user = new UserSummaryResponse();
					user.id = resultSet.getLong("id");
					user.name = resultSet.getString("name");

					response.add(user);
				}

				return response;
			}
		} catch (SQLException ex) {

			Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		throw new WebApplicationException("Unknown error occured", 500);

	}

	public User getSingle(Long id) {

		return User.findById(id);

	}

	public User updateUser(User user, Long id, UserRequest request) {

		User entity = User.findById(id);

		if (entity == null) {
			throw new WebApplicationException("User with these details does not exist.", 404);
		}

		entity.firstname = request.firstname;
		entity.lastname = request.lastname;
		entity.othername = request.othername;

		Role role = Role.findById(request.roleId);
		if (role == null) {
			throw new WebApplicationException("Untracable roles included.", 422);
		}
		if (!role.isApproved) {
			throw new WebApplicationException("Un approved roles included.", 422);
		}
		entity.role = role;
		entity.persist();

		return entity;
	}

	public User deactivateUser(Long id) {

		User entity = User.findById(id);

		if (entity == null) {
			throw new WebApplicationException("Role with these details does not exist.", 404);
		}

		entity.status = _StatusTypes_Enum.DEACTIVATED.toString();

		return entity;
	}

	public User activateUser(Long id) {

		User entity = User.findById(id);

		if (entity == null) {
			throw new WebApplicationException("Role with these details does not exist.", 404);
		}

		entity.status = _StatusTypes_Enum.ACTIVE.toString();

		return entity;
	}

	public User delete(Long id) {

		User entity = User.findById(id);

		if (entity == null) {
			throw new WebApplicationException("User with this Id does not exist.", 404);
		}

		entity.deleted = 1;

		return entity;
	}

}
