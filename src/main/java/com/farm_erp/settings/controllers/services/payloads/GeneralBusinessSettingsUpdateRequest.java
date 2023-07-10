package com.farm_erp.settings.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class GeneralBusinessSettingsUpdateRequest {
    @NotNull
    @Schema(required = true, example = "5")
    public String settingValue;

}
