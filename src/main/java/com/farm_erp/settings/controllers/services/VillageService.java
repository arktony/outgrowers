package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.VillageRequest;
import com.farm_erp.settings.domains.District;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class VillageService {

    public Village create(VillageRequest request, User user){
        Village exists = Village.findByName(request.name);
        if(exists != null) throw new WebApplicationException("Village with that name already exists",404);

        District district = District.findById(request.districtId);
        if(district == null) throw new WebApplicationException("Invalid district selected",404);

        Village zone = new Village(request.name, district);
        zone.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.VILLAGE.toString(),
                zone.id, null, jsonb.toJson(zone), user);
        trail.persist();

        return zone;
    }

    public Village update(Long id, VillageRequest request, User user){
        Village zone = Village.findById(id);
        if(zone == null) throw new WebApplicationException("Village not found",404);

        Village old = zone;

        Village exists = Village.findByNameExists(zone.id,request.name);
        if(exists != null) throw new WebApplicationException("Village with that name already exists",404);

        District district = District.findById(request.districtId);
        if(district == null) throw new WebApplicationException("Invalid district selected",404);

        zone.name = request.name;
        zone.district = district;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.VILLAGE.toString(),
                zone.id, jsonb.toJson(old), jsonb.toJson(zone), user);
        trail.persist();

        return zone;
    }

    public List<Village> get(Long districtId){
        District district = null;
        if(districtId != null) {
            district = District.findById(districtId);
            if (district == null) throw new WebApplicationException("Invalid district selected", 404);
        }

        return Village.findByDistrict(district);
    }

    public Village delete(Long id, User user){
        Village zone = Village.findById(id);
        if(zone == null) throw new WebApplicationException("Village not found",404);

        Village old = zone;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.VILLAGE.toString(),
                zone.id, jsonb.toJson(old), jsonb.toJson(zone), user);
        trail.persist();

        zone.deleted = 1;

        return zone;
    }
}
