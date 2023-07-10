package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.FarmerTypeRequest;
import com.farm_erp.settings.domains.FarmerType;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class FarmerTypeService {

    public FarmerType create(FarmerTypeRequest request, User user){
        FarmerType exists = FarmerType.find(null, request.name, request.code);
        if(exists != null) throw new WebApplicationException("Either the name or code already exists",404);

        FarmerType type = new FarmerType(request.name, request.code);
        type.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.FARMER_TYPE.toString(),
                type.id, null, jsonb.toJson(type), user);
        trail.persist();

        return type;
    }

    public FarmerType update(Long id, FarmerTypeRequest request, User user){
        FarmerType type = FarmerType.findById(id);
        if(type == null) throw new WebApplicationException("FarmerType not found",404);

        FarmerType exists = FarmerType.find(id, request.name, request.code);
        if(exists != null) throw new WebApplicationException("FarmerType already exists",404);

        FarmerType old = type;

        type.name = request.name;
        type.code = request.code;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.FARMER_TYPE.toString(),
                type.id, jsonb.toJson(old), jsonb.toJson(type), user);
        trail.persist();

        return type;
    }

    public List<FarmerType> get(){
        return FarmerType.listAll();
    }

    public FarmerType delete(Long id, User user){
        FarmerType type = FarmerType.findById(id);
        if(type == null) throw new WebApplicationException("FarmerType not found",404);

        FarmerType old = type;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.FARMER_TYPE.toString(),
                type.id, jsonb.toJson(old), jsonb.toJson(type), user);
        trail.persist();

        type.deleted = 1;

        return type;
    }
}
