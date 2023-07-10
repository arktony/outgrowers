package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class FarmerRequest {
    //farmer details
    @NotNull
    @Schema(required = true, example = "Mike")
    public String firstName;

    @NotNull
    @Schema(required = true, example = "Mutebi")
    public String surName;

    @Schema(required = false, example = "John")
    public String otherName;

    @NotNull
    @Schema(required = true, example = "07777777777")
    public String gender;

    @NotNull
    @Schema(required = true, example = "1")
    public Long districtOfficeId;

    @NotNull
    @Schema(required = true)
    public Long typeId;

    @Schema(required = false, enumeration = {"National Id", "Village Id", "Other Id"})
    public String identificationType;

    @Schema(required = false, example = "CM000499595959")
    public String identificationNumber;

    //farmer contact
    @Schema(required = false, example = "07777777777")
    public String airtel;

    @Schema(required = false, example = "07777777777")
    public String mtn;

    @Email
    @Schema(required = false, example = "admin@test.com")
    public String email;

    //farmer address
    @Schema(required = false, example = "Buikwe")
    public String village;

    @Schema(required = false, example = "Kitwe")
    public String parish;

    @Schema(required = false, example = "Yola")
    public String subcounty;

    @Schema(required = false, example = "Jinja")
    public String district;

    //next of kin
    @Schema(required = false, example = "Lubambula Deo")
    public String nokname;

    @Schema(required = false, example = "07777777777")
    public String nokcontact;

    @Schema(required = false, example = "Kitwe Jinja")
    public String nokaddress;

    //blocks
    @Schema(required = false)
    public List<FarmerBlockRequest> blocks = new ArrayList<>();
}
