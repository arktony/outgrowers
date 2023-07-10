package com.farm_erp.settings.controllers.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.settings.controllers.services.payloads.DistrictRequest;
import com.farm_erp.settings.domains.District;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

@ApplicationScoped
public class DistrictService {

    public District create(DistrictRequest request, User user) {
        District exists = District.findByCode(null, request.name, request.code);
        if (exists != null)
            throw new WebApplicationException("Either name or code already exists", 404);

        District district = new District(request.name, request.code);
        district.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.DISTRICTS.toString(),
                district.id, null, jsonb.toJson(district), user);
        trail.persist();

        return district;
    }

    public District update(Long id, DistrictRequest request, User user) {
        District district = District.findById(id);
        if (district == null)
            throw new WebApplicationException("District not found", 404);

        District exists = District.findByCode(id, request.name, request.code);
        if (exists != null)
            throw new WebApplicationException("Either name or code already exists", 404);

        District old = district;

        district.name = request.name;

        if (district.code != request.code) {
            // Adjust the farmer codes
            List<Farmer> farmers = Farmer.list("districtOffice.district", district);
            farmers.forEach(farmer -> {
                GeneralBusinessSettings set = GeneralBusinessSettings
                        .single(_SettingParameter_Enums.FARMER_CODE_PREFIX.toString());
                String ledgerT = farmer.registrationNumber
                        .replace(set.settingValue + farmer.districtOffice.district.code + farmer.type.code, "");
                farmer.registrationNumber = set.settingValue + request.code + farmer.type.code + ledgerT;
            });

            // Adjust the village codes
            List<Village> villages = Village.findByDistrict(district);
            villages.forEach(village -> {
                String ledgerT = village.code.replace(village.district.code, "");
                village.code = request.code + String.format("%01d", Integer.parseInt(ledgerT));
            });

            district.code = request.code;

        }

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.DISTRICTS.toString(),
                district.id, jsonb.toJson(old), jsonb.toJson(district), user);
        trail.persist();

        return district;
    }

    public List<District> get() {
        return District.listAll();
    }

    public District delete(Long id, User user) {
        District district = District.findById(id);
        if (district == null)
            throw new WebApplicationException("District not found", 404);

        District old = district;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.DISTRICTS.toString(),
                district.id, jsonb.toJson(old), jsonb.toJson(district), user);
        trail.persist();

        district.deleted = 1;

        return district;
    }
}
