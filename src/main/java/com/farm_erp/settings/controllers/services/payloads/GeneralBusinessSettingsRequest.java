package com.farm_erp.settings.controllers.services.payloads;

import com.farm_erp.settings.statics._SettingParameter_Enums;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

public class GeneralBusinessSettingsRequest {
    @NotNull
    @Schema(required = true, example = "5")
    public String settingValue;

    @NotNull
    @Schema(required = true, example = "Maturity Period")
    public _SettingParameter_Enums setting;

    public void setSetting(String setting) {
        if (_SettingParameter_Enums.getEnum(setting) == null)
            throw new WebApplicationException("Invalid setting selected", 404);

        this.setting = _SettingParameter_Enums.getEnum(setting);
    }
}
