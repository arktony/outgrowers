package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AidBlockRequest {

    @Schema(required = false, example = "5")
    public String numberOfAcres;

    @NotNull
    @Schema(required = true)
    public Long aidId;

    public BigDecimal amount;
}
