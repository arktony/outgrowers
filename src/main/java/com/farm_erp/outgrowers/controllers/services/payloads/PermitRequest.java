package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class PermitRequest {
    @NotNull
    @Schema(required = true)
    public Integer numberOfDays;
}
