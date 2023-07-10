package com.farm_erp.auth.controllers.services;

import com.farm_erp.auth.controllers.services.models.BusinessRequest;
import com.farm_erp.auth.domain.User;
import com.farm_erp.fileresources.controllers.services.FileRequest;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.fileresources.domain.FileResource;
import com.farm_erp.settings.domains.Business;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class BusinessService {

    @Inject
    FileService fileService;

    public Business updateLogo(Long businessId, FileRequest file, User user) {

        Business business = Business.findById(businessId);
        if (business == null) {
            throw new WebApplicationException("Business not found!", 404);
        }

        Business oldData = business;

        FileResource newFile;
        if (business.logo == null) {
            newFile = fileService.create(file);
        } else {
            newFile = fileService.update(business.logo.id, file);
        }
        business.logo = newFile;

        business.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.BUSINESS.toString(), business.id,
                jsonb.toJson(oldData), jsonb.toJson(business), user);
        trail.persist();

        return business;
    }

    public Business update(Long businessId, BusinessRequest request, User user) {

        Business business = Business.findById(businessId);
        if (business == null) {
            throw new WebApplicationException("Business not found!", 404);
        }

        Business oldData = business;

        business.name = request.name;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.BUSINESS.toString(), business.id,
                jsonb.toJson(oldData), jsonb.toJson(business), user);
        trail.persist();

        return business;
    }
}
