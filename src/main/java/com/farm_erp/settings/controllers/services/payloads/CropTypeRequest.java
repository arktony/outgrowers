package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class CropTypeRequest {
    @NotNull
    @Schema(required = true, example = "Plant")
    public String name;

    @NotNull
    @Schema(required = true, example = "45")
    public Double expectedTonnesPerAcre;

    @NotNull
    @Schema(required = true, example = "1")
    public Integer position;

    @NotNull
    @Schema(required = true, example = "PL")
    public String code;
}
