package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class FarmerBlockRequest {
    //block
    @NotNull
    @Schema(required = true, example = "40")
    public Double area;

    @NotNull
    @Schema(required = true, example = "40")
    public Double distance;

    @NotNull
    @Schema(required = true, example = "1")
    public Long villageId;

    @NotNull
    @Schema(required = true, example = "1")
    public Long caneVarietyId;

    @NotNull
    @Schema(required = true, enumeration = {"HIRED", "OWNED"})
    public String landOwnership;

    // block crop type
    @NotNull
    @Schema(required = true, example = "01/02/2022")
    @JsonbDateFormat("dd/MM/yyyy")
    public LocalDate plantingDate;

    @NotNull
    @Schema(required = true, example = "1")
    public Long cropTypeId;
}
