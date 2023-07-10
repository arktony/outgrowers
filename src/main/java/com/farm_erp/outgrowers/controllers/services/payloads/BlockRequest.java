package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class BlockRequest {
    @NotNull
    @Schema(required = true, example = "40")
    public Double area;

    @NotNull
    @Schema(required = true, example = "40")
    public Double distance;

    @NotNull
    @Schema(required = true, enumeration = {"HIRED", "OWNED"})
    public String landOwnership;

    @NotNull
    @Schema(required = true, example = "1")
    public Long farmerId;

    @NotNull
    @Schema(required = true, example = "1")
    public Long caneVarietyId;

    @NotNull
    @Schema(required = true, example = "1")
    public Long villageId;
}
