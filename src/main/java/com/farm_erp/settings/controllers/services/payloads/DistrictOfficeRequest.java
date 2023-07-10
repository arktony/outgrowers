package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class DistrictOfficeRequest {
    @NotNull
    @Schema(required = true, example = "Bunya East")
    public String name;

    @NotNull
    @Schema(required = true, example = "1")
    public Long districtId;

}
