package com.farm_erp.weigh_bridge.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class UnLoadedRequest {

    @NotNull
    @Schema(required = true)
    public Double tareWeight;

    @NotNull
    @Schema(required = true)
    public String identifier;

    @NotNull
    @Schema(required = true)
    @JsonbDateFormat("dd/MM/yyyy HH:mm:ss")
    public LocalDateTime unloadTime;
}
