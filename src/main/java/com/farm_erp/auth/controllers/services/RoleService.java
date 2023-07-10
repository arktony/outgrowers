package com.farm_erp.auth.controllers.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.controllers.services.models.RoleRequest;
import com.farm_erp.auth.controllers.services.models.RoleSummaryResponse;
import com.farm_erp.auth.domain.*;
import com.farm_erp.statics._StatusTypes_Enum;
import io.agroal.api.AgroalDataSource;

@ApplicationScoped
public class RoleService {

    @Inject
    AgroalDataSource dataSource;

    public Role create(User user, RoleRequest request) {
        if (request.name.equals("System Administrator")) {
            throw new WebApplicationException("'System Administrator' is a reserved system name", 403);
        }

        Optional<Role> roleExists = Role.find("name", request.name.toUpperCase()).singleResultOptional();
        if (roleExists.isPresent()) {
            activate(roleExists.get().id);
        }

        Role role = new Role();
        role.name = request.name.toUpperCase();
        role.type = request.type;
        role.description = request.description;
        role.isApproved = Boolean.TRUE;
        role.status = _StatusTypes_Enum.ACTIVE.toString();
        role.persist();

        List<Category> cats = Category.listAll();
        for (Category cat : cats) {
            RoleCategory rcat = new RoleCategory(cat.name, cat.nameEnum, cat.description, role, cat);
            rcat.persist();

            for (CategoryItem itm : cat.items) {
                RoleCategoryItem ritm = new RoleCategoryItem(itm.name, itm.nameEnum, itm.description, rcat);
                ritm.persist();

                for (CategoryPrivilege p : itm.privileges) {
                    RoleCategoryPrivilege priv = new RoleCategoryPrivilege(p.name, p.nameEnum, ritm);
                    priv.persist();
                }
            }
        }

        return role;
    }

    public List<Role> get() {
        return Role.list("status", _StatusTypes_Enum.ACTIVE.toString());
    }

    public List<RoleSummaryResponse> getSummary() {

        List<RoleSummaryResponse> response = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select "
                     + "id, "
                     + "name "
                     + "from Role "
                     + "where status = '"
                     + _StatusTypes_Enum.ACTIVE.toString()
                     + "' and name <> 'Super Administrator'");
             ResultSet resultSet = statement.executeQuery();) {

            while (resultSet.next()) {
                RoleSummaryResponse user = new RoleSummaryResponse();
                user.id = resultSet.getLong("id");
                user.name = resultSet.getString("name");

                response.add(user);
            }

            return response;
        } catch (SQLException ex) {

            Logger.getLogger(RoleService.class.getName()).log(Level.SEVERE, null, ex);

        }

        throw new WebApplicationException("Unknown error occured", 500);

    }

    public Role getSingle(Long id) {
        return Role.findById(id);
    }

    public Role update(Long id, RoleRequest request) {

        Role entity = Role.findById(id);

        if (entity != null) {
            if ((entity.name.equals("Super Administrator"))) {
                throw new WebApplicationException("Role with name 'admin' is a reserved system name", 403);
            }

        } else {
            throw new WebApplicationException("Role with these details does not exist.", 404);

        }

        entity.name = request.name.toUpperCase();
        entity.description = request.description;
        entity.persist();

        return entity;
    }

    public Role activate(Long id) {
        Role entity = Role.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Selected role does not exist.", 404);
        }

        entity.status = _StatusTypes_Enum.ACTIVE.toString();
        entity.persist();

        return entity;
    }

    public Role deactivate(Long id) {
        Role entity = Role.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Selected role does not exist.", 404);
        }
        if ((entity.name.equals("Super Administrator"))) {
            throw new WebApplicationException("Action Forbidden!!", 403);
        }

        entity.status = _StatusTypes_Enum.DEACTIVATED.toString();
        entity.persist();

        return entity;
    }

    public Role delete(Long id) {

        Role entity = Role.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Role with this Id does not exist.", 404);
        }

        Role deleted = entity;

        entity.deleted = 1;

        return deleted;
    }

}
