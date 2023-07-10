/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.auth.controllers.services.models;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aiden
 */
public class RoleRequest {

    @NotNull
    @Schema(required = true, example = "Human Resource")
    public String name;

    @NotNull
    @Schema(required = true, enumeration = {"Administrator", "Operations"})
    public String type;

    public String description;

    public void setType(String type) {
        List<String> types = new ArrayList<>();
        types.add("Administrator");
        types.add("Operations");

        if(types.contains(type)) {
            this.type = type;
        }else{
            throw new WebApplicationException("Invalid role type selected",404);
        }
    }
}
