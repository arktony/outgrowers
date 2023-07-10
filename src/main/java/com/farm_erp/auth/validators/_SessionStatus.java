package com.farm_erp.auth.validators;

import com.farm_erp.auth.domain.User;

public class _SessionStatus {

	public Boolean status;

	public User user;

	public String accessKey;

	public String response;

	public _SessionStatus() {
		super();
	}

	public _SessionStatus(Boolean status) {
		this.status = status;
	}

	public _SessionStatus(Boolean status, User user) {
		this.status = status;
		this.user = user;
	}

	public _SessionStatus(Boolean status, String response) {
		super();
		this.status = status;
		this.response = response;
	}

}
