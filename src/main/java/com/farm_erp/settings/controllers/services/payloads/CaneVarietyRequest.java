package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CaneVarietyRequest {
    @NotNull
    @Schema(required = true, example = "Green")
    public String name;

    @NotNull
    @Schema(required = false, example = "Green cane")
    public String description;

}
