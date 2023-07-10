package com.farm_erp.auth.controllers.services.models;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class BusinessRequest {
    @Schema(required = true)
    public String name;
}
