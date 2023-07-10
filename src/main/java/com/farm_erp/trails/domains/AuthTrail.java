package com.farm_erp.trails.domains;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.farm_erp.statics.TimeConverter;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AuthTrail extends PanacheEntity {

	@NotBlank
	@Column(nullable = false)
	private String ipAddress;

	public String email;

	public String city;

	public String latitude;

	public String longitude;

	public String deviceDetail;

	public Integer attempts = 1;

	public LocalDateTime entryDate = LocalDateTime.now();

	public Long nextAttempt;

	public Boolean isValid;

	public Boolean isMyDevice = Boolean.FALSE;

	@NotBlank
	@Column(nullable = false)
	private String status;

	public AuthTrail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static Optional<AuthTrail> getValidFailedAttemptsByType(String email, String ipAddress, String attemptType) {

		return AuthTrail.find(
				"email = ?1 and ipAddress = ?2 and status = ?3 and isValid = ?4 and entryDate >= ?5 and entryDate <= ?6",
				email, ipAddress, attemptType, Boolean.TRUE,
				TimeConverter.LocalDate_to_EpochMilli_DayStart(LocalDate.now()),
				TimeConverter.LocalDate_to_EpochMilli_DayEnd(LocalDate.now())).singleResultOptional();

	}

	public static Optional<AuthTrail> getInitialValidSignUpAttempt(String email, String deviceDetail) {

		return AuthTrail.find("email = ?1 and deviceDetail = ?2 and status = ?3 and isValid = ?4", email, deviceDetail,
				"SIGNUP", Boolean.TRUE).singleResultOptional();

	}

}
