package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.CaneVarietyRequest;
import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class CaneVarietyService {
    public CaneVariety create(CaneVarietyRequest request, User user) {

        CaneVariety exists = CaneVariety.findByName(request.name);
        if (exists != null) throw new WebApplicationException("Cane Variety with that name already exists!", 406);

        CaneVariety aid = new CaneVariety(request.name, request.description);
        aid.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.CANE_VARIETY.toString(), aid.id,
                null, jsonb.toJson(aid), user);
        trail.persist();

        return aid;
    }

    public CaneVariety update(Long id, CaneVarietyRequest request, User user) {

        CaneVariety aid = CaneVariety.findById(id);
        if (aid == null) throw new WebApplicationException("Invalid cane variety  selected!", 404);

        CaneVariety exists = CaneVariety.findByNameExists(request.name, id);
        if (exists != null) throw new WebApplicationException("Cane Variety with that name already exists!", 406);

        CaneVariety oldData = aid;

        aid.description = request.description;
        aid.name = request.name;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.CANE_VARIETY.toString(), aid.id,
                jsonb.toJson(oldData), jsonb.toJson(aid), user);
        trail.persist();

        return aid;
    }

    public List<CaneVariety> get() {
        return CaneVariety.listAll(Sort.by("name"));
    }

    public CaneVariety delete(Long id, User user) {
        CaneVariety aid = CaneVariety.findById(id);
        if (aid == null) throw new WebApplicationException("Invalid cane variety selected!", 404);

        CaneVariety oldData = aid;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.CANE_VARIETY.toString(), aid.id,
                jsonb.toJson(oldData), jsonb.toJson(aid), user);
        trail.persist();

        aid.deleted = 1;

        return aid;
    }
}
