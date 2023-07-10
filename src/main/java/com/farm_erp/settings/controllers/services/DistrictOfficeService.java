package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.DistrictOfficeRequest;
import com.farm_erp.settings.domains.District;
import com.farm_erp.settings.domains.DistrictOffice;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class DistrictOfficeService {

    public DistrictOffice create(DistrictOfficeRequest request, User user){
        DistrictOffice exists = DistrictOffice.find(request.name);
        if(exists != null) throw new WebApplicationException("District Office already exists",404);

        District zoner = District.findById(request.districtId);
        if(zoner == null) throw new WebApplicationException("Invalid district selected",404);

        DistrictOffice zone = new DistrictOffice(request.name, zoner);
        zone.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.DISTRICT_OFFICES.toString(),
                zone.id, null, jsonb.toJson(zone), user);
        trail.persist();

        return zone;
    }

    public DistrictOffice update(Long id, DistrictOfficeRequest request, User user){
        DistrictOffice zone = DistrictOffice.findById(id);
        if(zone == null) throw new WebApplicationException("District Office not found",404);

        DistrictOffice exists = DistrictOffice.exists(zone.id, request.name);
        if(exists != null) throw new WebApplicationException("District Office already exists",404);

        District zon = District.findById(request.districtId);
        if(zon == null) throw new WebApplicationException("Invalid district selected",404);

        DistrictOffice old = zone;

        zone.name = request.name;
        zone.district = zon;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.DISTRICT_OFFICES.toString(),
                zone.id, jsonb.toJson(old), jsonb.toJson(zone), user);
        trail.persist();

        return zone;
    }

    public List<DistrictOffice> get(Long zoneId){
        District district = null;
        if(zoneId != null) {
            district = District.findById(zoneId);
            if (district == null) throw new WebApplicationException("Invalid district selected", 404);
        }

        return DistrictOffice.findByZone(district);
    }

    public DistrictOffice delete(Long id, User user){
        DistrictOffice zone = DistrictOffice.findById(id);
        if(zone == null) throw new WebApplicationException("District Office not found",404);

        DistrictOffice old = zone;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.DISTRICT_OFFICES.toString(),
                zone.id, jsonb.toJson(old), jsonb.toJson(zone), user);
        trail.persist();

        zone.deleted = 1;

        return zone;
    }
}
