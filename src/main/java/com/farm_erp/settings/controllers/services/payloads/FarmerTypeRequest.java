package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FarmerTypeRequest {
    @NotNull
    @Schema(required = true, example = "Nuclear Farmer")
    public String name;

    @NotNull
    @Schema(required = false, example = "NF")
    public String code;


}
