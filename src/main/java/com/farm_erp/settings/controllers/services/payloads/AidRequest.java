package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AidRequest {
    @NotNull
    @Schema(required = true, example = "Ploughing Aid")
    public String name;

    @NotNull
    @Schema(required = false, example = "Ploughing Aid")
    public String description;

    @NotNull
    @Schema(required = true, example = "100000")
    public BigDecimal costPerAcre;

    public Boolean isIndependent;

}
