/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.auth.controllers.services.models;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class LoginRequest {

    @Schema(required = true, example = "admin@gmail.com")
    public String email;

    @Schema(required = true, example = "123")
    public String password;

}
