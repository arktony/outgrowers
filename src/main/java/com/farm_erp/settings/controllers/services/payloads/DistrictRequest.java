package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class DistrictRequest {
    @NotNull
    @Schema(required = true, example = "North")
    public String name;

    @NotNull
    @Schema(required = true, example = "North")
    public String code;
}
