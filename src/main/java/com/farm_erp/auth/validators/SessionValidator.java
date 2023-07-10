/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.auth.validators;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.farm_erp.auth.domain.Role;
import com.farm_erp.auth.domain.RoleCategory;
import com.farm_erp.auth.domain.RoleCategoryItem;
import com.farm_erp.auth.domain.RoleCategoryPrivilege;
import com.farm_erp.auth.domain.User;
import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.configurations.security.CustomAwareJWTAuthMechanism;

@ApplicationScoped
public class SessionValidator {

	public static _SessionStatus validate(String useremail, CategoryEnum category, CategoryItemEnum categoryItem,
			Privilege privilege) {

		_SessionStatus sessionstatus = new _SessionStatus();

		if (useremail.equals("anonymous")) {
			sessionstatus.status = false;
			sessionstatus.response = "Anonymous user!";

			return sessionstatus;
		}

		Optional<User> user = User.findByEmail(useremail);
		if (user.isEmpty()) {
			sessionstatus.status = false;
			sessionstatus.response = "Invalid Credentials!";

			return sessionstatus;
		}
		sessionstatus.user = user.get();

		sessionstatus.status = isAllowed(user.get().role, category, categoryItem, privilege);
		if (!sessionstatus.status) {
			sessionstatus.response = "User unauthorized";
		}

		return sessionstatus;
	}

	public static _SessionStatus validateOpen(String useremail) {

		_SessionStatus sessionstatus = new _SessionStatus();

		if (useremail.equals("anonymous")) {
			sessionstatus.status = false;
			sessionstatus.response = "Anonymous user!";

			return sessionstatus;
		}

		Optional<User> user = User.findByEmail(useremail);
		if (user.isEmpty()) {
			sessionstatus.status = false;
			sessionstatus.response = "Invalid Credentials!";

			return sessionstatus;
		}
		sessionstatus.user = user.get();
		sessionstatus.status = true;

		return sessionstatus;
	}

	public static _SessionStatus validateApplication(String accesstoken) {

		CustomAwareJWTAuthMechanism authMechanism = new CustomAwareJWTAuthMechanism();

		_SessionStatus sessionstatus = new _SessionStatus();

		if (authMechanism.validateKey(accesstoken)) {
			sessionstatus.status = true;
			sessionstatus.accessKey = accesstoken;
		} else {
			sessionstatus.status = false;
			sessionstatus.response = "Anonymous user!";
		}

		sessionstatus.status = true;

		return sessionstatus;
	}

	public static Boolean isAllowed(Role role, CategoryEnum category, CategoryItemEnum categoryItem,
			Privilege privilege) {
		if (role.type.equals("Super Administrator")) {
			return Boolean.TRUE;
		}
		for (RoleCategory cat : role.categories) {
			if (cat.nameEnum.equals(category.toString())) {
				if (cat.access) {
					for (RoleCategoryItem itm : cat.items) {
						if (itm.nameEnum.equals(categoryItem.toString())) {
							if (itm.access) {
								for (RoleCategoryPrivilege pri : itm.privileges) {
									if (pri.nameEnum.equals(privilege.toString())) {
										return pri.access;
									}
								}
							}
						}
					}
				}
			}
		}
		return Boolean.FALSE;
	}
}
