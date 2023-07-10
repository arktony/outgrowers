package com.farm_erp.weigh_bridge.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class RFIDRequest {
    @NotNull
    @Schema(required = true, example = "12345")
    public String rfid;
}
