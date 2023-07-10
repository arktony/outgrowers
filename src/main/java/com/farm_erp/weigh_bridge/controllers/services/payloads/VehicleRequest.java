package com.farm_erp.weigh_bridge.controllers.services.payloads;

import com.farm_erp.weigh_bridge.statics.VehicleStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

public class VehicleRequest {

    @Schema(required = false, example = "12345")
    public String rfid;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String TIN;

    @NotNull
    @Schema(required = true, example = "9KDOJ993I30EK")
    public String vehicleNumber;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String registrationNumber;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String make;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String color;

    @NotNull
    @Schema(required = false, example = "9KDOJ993I30EK")
    public String chassisNumber;

    @NotNull
    @Schema(required = false, enumeration = {"Important", "Critical", "Normal"})
    public VehicleStatus priority;

    @NotNull
    @Schema(required = true)
    public Long transporterId;

    public void setPriority(String priority) {
        if (VehicleStatus.getEnum(priority) == null)
            throw new WebApplicationException("Invalid priority selected", 404);

        this.priority = VehicleStatus.getEnum(priority);
    }
}
