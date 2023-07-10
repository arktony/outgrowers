package com.farm_erp.settings.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.settings.controllers.services.payloads.CropTypeRequest;
import com.farm_erp.settings.domains.CropType;
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
public class CropTypeService {
    public CropType create(CropTypeRequest request, User user) {

        CropType exists = CropType.findByName(request.name);
        if (exists != null) throw new WebApplicationException("Crop type with that name already exists!", 406);

        CropType exists1 = CropType.findByCode(request.code);
        if (exists1 != null) throw new WebApplicationException("Crop type with that code already exists!", 406);

        CropType exists2 = CropType.findByPosition(request.position);
        if (exists2 != null) throw new WebApplicationException("Crop type with that position already exists!", 406);

        CropType cropType = new CropType(request.name, request.code, request.expectedTonnesPerAcre, request.position);
        cropType.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.CROP_TYPES.toString(), cropType.id,
                null, jsonb.toJson(cropType), user);
        trail.persist();

        return cropType;
    }

    public CropType update(Long id, CropTypeRequest request, User user) {

        CropType cropType = CropType.findById(id);
        if (cropType == null) throw new WebApplicationException("Invalid crop type selected!", 404);

        CropType exists1 = CropType.findByCodeExists(request.code, id);
        if (exists1 != null) throw new WebApplicationException("Crop type with that code already exists!", 406);

        CropType exists2 = CropType.findByPositionExists(request.position, id);
        if (exists2 != null) throw new WebApplicationException("Crop type with that position already exists!", 406);

        CropType oldData = cropType;

        cropType.code = request.code;
        cropType.expectedTonnesPerAcre = request.expectedTonnesPerAcre;
        cropType.position = request.position;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.CROP_TYPES.toString(), cropType.id,
                jsonb.toJson(oldData), jsonb.toJson(cropType), user);
        trail.persist();

        return cropType;
    }

    public List<CropType> get() {
        return CropType.listAll(Sort.by("name"));
    }

    public CropType delete(Long id, User user) {
        CropType cropType = CropType.findById(id);
        if (cropType == null) throw new WebApplicationException("Invalid crop type selected!", 404);

        CropType oldData = cropType;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.CROP_TYPES.toString(), cropType.id,
                jsonb.toJson(oldData), jsonb.toJson(cropType), user);
        trail.persist();

        cropType.deleted = 1;

        return cropType;
    }
}
