package com.farm_erp.auth.domain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.fileresources.domain.FileResource;
import com.farm_erp.settings.domains.Business;
import com.farm_erp.statics._StatusTypes_Enum;

import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Where(clause = "DELETED = 0")
public class User extends PanacheEntity {

	@Size(max = 50)
	@Email
	public String email;

	@Column(name = "DELETED")
	public Integer deleted = 0;

	@NotBlank
	@JsonbTransient
	@Column(nullable = false)
	public String password;

	@NotBlank
	@Column(nullable = false)
	public String firstname;

	@NotBlank
	@Size(max = 120)
	@Column(nullable = false)
	public String lastname;

	public String othername;

	public String phone;

	public String status = _StatusTypes_Enum.ACTIVE.toString();

	// Mapping

	// relationships
	@OneToOne
	@JoinColumn(nullable = true)
	public FileResource photo;

	@ManyToOne
	@JoinColumn(nullable = false)
	public Role role;

	@ManyToOne
	@JoinColumn(nullable = false)
	public Business business;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static User findSuperAdmin(){
		return find("role.name", "Super Administrator").firstResult();
	}
	public static Optional<User> findByEmail(String email) {

		return find("email", email).singleResultOptional();
	}

	public static User login(String username, String password) {

		Optional<User> user = User.find("email = ?1", username).singleResultOptional();

		if (!user.isPresent()) {
			throw new WebApplicationException("Invalid credentials! Access denied!", 401);
		}

		if (verifyPassword(password, user.get().password)) {
			return user.get();
		} else {
			throw new WebApplicationException("Invalid credentials! Access denied!", 401);
		}
	}

	public static Boolean verifyPassword(String originalPwd, String encryptedPwd) {
		Logger logger = LoggerFactory.getLogger(User.class);

		try {
			// convert encrypted password string to a password key
			Password rawPassword = ModularCrypt.decode(encryptedPwd);

			try {
				// create the password factory based on the bcrypt algorithm
				PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);

				try {

					// create encrypted password based on stored string
					BCryptPassword restored = (BCryptPassword) factory.translate(rawPassword);

					// verify restored password against original
					return factory.verify(restored, originalPwd.toCharArray());

				} catch (InvalidKeyException e) {
					logger.error("Invalid password key: {}", e.getMessage());

				}

			} catch (NoSuchAlgorithmException e) {
				logger.error("Invalid Algorithm: {}", e.getMessage());

			}

		} catch (InvalidKeySpecException e) {
			logger.error("Invalid key: {}", e.getMessage());

		}

		return false;

	}

	public static void updatePassword(User user, String password) {
		user.password = BcryptUtil.bcryptHash(password);
	}

}
