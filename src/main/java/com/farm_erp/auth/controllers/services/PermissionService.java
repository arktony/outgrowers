package com.farm_erp.auth.controllers.services;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.domain.RoleCategory;
import com.farm_erp.auth.domain.RoleCategoryItem;
import com.farm_erp.auth.domain.RoleCategoryPrivilege;
import com.farm_erp.auth.domain.User;
import com.farm_erp.statics._CategoryEnums;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

@ApplicationScoped
public class PermissionService {
    public RoleCategoryPrivilege update(Long id, User user) {
        RoleCategoryPrivilege pri = RoleCategoryPrivilege.findById(id);
        if (pri == null) throw new WebApplicationException("Invalid privilege selected!", 404);

        RoleCategoryPrivilege oldData = pri;

        pri.access = !oldData.access;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.ROLES.toString(), pri.id,
                 jsonb.toJson(oldData), jsonb.toJson(pri), user);
        trail.persist();

        return pri;
    }

    public RoleCategory updateRoleCategory(Long id, User user) {
        RoleCategory pri = RoleCategory.findById(id);
        if (pri == null) throw new WebApplicationException("Invalid privilege selected!", 404);

        RoleCategory oldData = pri;

        pri.access = !oldData.access;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.ROLES.toString(), pri.id,
                jsonb.toJson(oldData), jsonb.toJson(pri), user);
        trail.persist();

        return pri;
    }

    public RoleCategoryItem updateRoleCategoryItem(Long id, User user) {
        RoleCategoryItem pri = RoleCategoryItem.findById(id);
        if (pri == null) throw new WebApplicationException("Invalid privilege selected!", 404);

        RoleCategoryItem oldData = pri;

        pri.access = !oldData.access;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.ROLES.toString(), pri.id,
                jsonb.toJson(oldData), jsonb.toJson(pri), user);
        trail.persist();

        return pri;
    }

    public List<CData> getCategories(){
        List<CData> data = new ArrayList<>();
        for(_CategoryEnums e : _CategoryEnums.values()){
            data.add(new CData(e.label, e.toString()));
        }
        return data;
    }
}

class CData{
    public String label;
    public String enumString;

    public CData(String label, String enumString) {
        this.label = label;
        this.enumString = enumString;
    }
}