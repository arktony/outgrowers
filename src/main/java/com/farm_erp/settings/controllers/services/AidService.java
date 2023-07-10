package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.AidRequest;
import com.farm_erp.settings.domains.Aid;
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
public class AidService {
    public Aid create(AidRequest request, User user) {

        Aid exists = Aid.findByName(request.name);
        if (exists != null) throw new WebApplicationException("Aid with that name already exists!", 406);

        Aid aid = new Aid(request.name, request.description, request.costPerAcre, request.isIndependent);
        aid.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.AID.toString(), aid.id,
                null, jsonb.toJson(aid), user);
        trail.persist();

        return aid;
    }

    public Aid update(Long id, AidRequest request, User user) {

        Aid aid = Aid.findById(id);
        if (aid == null) throw new WebApplicationException("Invalid aid  selected!", 404);

        Aid exists = Aid.findByNameExists(request.name, id);
        if (exists != null) throw new WebApplicationException("Aid with that name already exists!", 406);

        Aid oldData = aid;

        aid.description = request.description;
        aid.name = request.name;
        aid.costPerAcre = request.costPerAcre;
        aid.isIndependent = request.isIndependent;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.AID.toString(), aid.id,
                jsonb.toJson(oldData), jsonb.toJson(aid), user);
        trail.persist();

        return aid;
    }

    public List<Aid> get() {
        return Aid.listAll(Sort.by("name"));
    }

    public Aid delete(Long id, User user) {
        Aid aid = Aid.findById(id);
        if (aid == null) throw new WebApplicationException("Invalid aid selected!", 404);

        Aid oldData = aid;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.AID.toString(), aid.id,
                jsonb.toJson(oldData), jsonb.toJson(aid), user);
        trail.persist();

        aid.deleted = 1;

        return aid;
    }
}
