package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class VillageRequest {
    @NotNull
    @Schema(required = true, example = "Musoli")
    public String name;

    @NotNull
    @Schema(required = true, example = "111001")
    public Long districtId;
}
