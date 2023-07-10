package com.farm_erp.weigh_bridge.controllers.services.payloads;

import com.farm_erp.weigh_bridge.statics.VehicleStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

public class PriorityRequest {

    @NotNull
    @Schema(required = true, enumeration = {"Important", "Critical", "Normal"})
    public VehicleStatus priority;

    public void setPriority(String priority) {
        if (VehicleStatus.getEnum(priority) == null)
            throw new WebApplicationException("Invalid priority selected", 404);

        this.priority = VehicleStatus.getEnum(priority);
    }
}
