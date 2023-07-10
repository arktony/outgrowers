package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.GeneralBusinessSettingsRequest;
import com.farm_erp.settings.controllers.services.payloads.GeneralBusinessSettingsUpdateRequest;
import com.farm_erp.settings.domains.CanePrice;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class GBSettingsService {

    public GeneralBusinessSettings create(GeneralBusinessSettingsRequest request, User user) {
        GeneralBusinessSettings exists = GeneralBusinessSettings.single(request.setting.label);

        if (exists != null)
            throw new WebApplicationException("Setting already exists.", 409);

        GeneralBusinessSettings set = new GeneralBusinessSettings(request.setting.toString(), request.settingValue);
        set.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.GENERAL_BUSINESS_SETTINGS.toString(), set.id,
                null, jsonb.toJson(set), user);
        trail.persist();

        return set;
    }

    public GeneralBusinessSettings update(Long id, GeneralBusinessSettingsUpdateRequest request, User user) {
        GeneralBusinessSettings set = GeneralBusinessSettings.findById(id);
        if (set == null)
            throw new WebApplicationException("Invalid setting selected", 404);

        GeneralBusinessSettings old = set;

        if(set.settingParameter.equals(_SettingParameter_Enums.PAYMENT_PER_TONNE.label)){
            CanePrice prix = new CanePrice(new BigDecimal(old.settingValue), new BigDecimal(request.settingValue), user);
            prix.persist();
        }

        set.settingValue = request.settingValue;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.GENERAL_BUSINESS_SETTINGS.toString(), set.id,
                jsonb.toJson(old), jsonb.toJson(set), user);
        trail.persist();

        return set;
    }

    public List<GeneralBusinessSettings> getSettings(){
        return GeneralBusinessSettings.listAll();
    }

    public GeneralBusinessSettings delete(Long id, User user){
        GeneralBusinessSettings set = GeneralBusinessSettings.findById(id);
        if (set == null)
            throw new WebApplicationException("Invalid setting selected", 404);

        GeneralBusinessSettings old = set;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.GENERAL_BUSINESS_SETTINGS.toString(), set.id,
                jsonb.toJson(old), jsonb.toJson(set), user);
        trail.persist();

        set.deleted = 1;
        return old;
    }
}
