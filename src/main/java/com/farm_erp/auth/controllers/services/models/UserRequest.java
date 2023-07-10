/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.auth.controllers.services.models;

import javax.validation.constraints.NotNull;

import com.farm_erp.fileresources.controllers.services.FileRequest;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserRequest {

	@Schema(required = true, example = "aide@gmail.com")
	public String email;

	@Schema(required = true, example = "Aiden")
	public String firstname;

	@Schema(required = true, example = "Aiden 2")
	public String lastname;

	public String othername;

	public String phone;

	public Boolean isSuperAdmin;

	@NotNull
	@Schema(required = true)
	public Long roleId;

	public FileRequest photo;

}
