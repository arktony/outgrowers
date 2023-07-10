package com.farm_erp.weigh_bridge.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransportFaresRequest {
    @NotNull
    @Schema(required = true, example = "0")
    public Double fromDistance;

    @NotNull
    @Schema(required = true, example = "0")
    public BigDecimal cost;

    public Double toDistance;

    public Boolean andAbove = Boolean.FALSE;


}
