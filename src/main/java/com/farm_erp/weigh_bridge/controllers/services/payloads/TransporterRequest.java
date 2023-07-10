package com.farm_erp.weigh_bridge.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class TransporterRequest {

    @Schema(required = true, example = "Mike Mutyaba")
    public String name;

    @NotNull
    @Schema(required = false, example = "Mike Mutebi")
    public String accountName;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String accountNumber;

    public List<VehicleRequest> vehicles = new ArrayList<>();

}
