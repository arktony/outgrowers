package com.farm_erp.weigh_bridge.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.weigh_bridge.controllers.services.payloads.TransporterRequest;
import com.farm_erp.weigh_bridge.controllers.services.payloads.VehicleRequest;
import com.farm_erp.weigh_bridge.domains.Transporter;
import com.farm_erp.weigh_bridge.domains.Vehicle;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class TransporterService {

    public Transporter create(TransporterRequest request, User user) {

        Transporter transporter = Transporter.findByNameAccount(request.accountName, request.accountNumber, null);
        if (transporter != null) throw new WebApplicationException("Transporter already exists!", 404);

        Transporter Transporter = new Transporter(request.name, request.accountName, request.accountNumber);
        Transporter.persist();

        if(request.vehicles != null && request.vehicles.size() > 0){
            for(VehicleRequest request1 : request.vehicles){
                Vehicle vehicle = new Vehicle(
                        request1.rfid,
                        request1.TIN,
                        request1.vehicleNumber,
                        request1.registrationNumber,
                        request1.make,
                        request1.color,
                        request1.chassisNumber,
                        request1.priority.toString(),
                        Transporter
                );
                vehicle.persist();
            }
        }

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.TRANSPORTERS.toString(), Transporter.id,
                null, jsonb.toJson(Transporter), user);
        trail.persist();


        return Transporter;
    }

    public Transporter update(Long id, TransporterRequest request, User user) {

        Transporter transporter = Transporter.findById(id);
        if (transporter == null) throw new WebApplicationException("Invalid Transporter selected!", 404);

        Transporter exist = Transporter.findByNameAccount(request.accountName, request.accountNumber, id);
        if (exist != null) throw new WebApplicationException("Transporter already exists with those details", 404);

        Transporter oldData = transporter;

        transporter.name = request.name;
        transporter.accountName = request.accountName;
        transporter.accountNumber = request.accountNumber;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.TRANSPORTERS.toString(), transporter.id,
                jsonb.toJson(oldData), jsonb.toJson(transporter), user);
        trail.persist();

        return transporter;
    }

    public List<Transporter> get() {
        return Transporter.listAll(Sort.by("name"));
    }

    public List<Vehicle> getTransporterVehicles(Long id){
        Transporter transporter = Transporter.findById(id);
        if (transporter == null) throw new WebApplicationException("Invalid Transporter selected!", 404);

        return transporter.vehicles;
    }

    public Transporter delete(Long id, User user) {
        Transporter transporter = Transporter.findById(id);
        if (transporter == null) throw new WebApplicationException("Invalid Transporter selected!", 404);

        Transporter oldData = transporter;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.TRANSPORTERS.toString(), transporter.id,
                jsonb.toJson(oldData), jsonb.toJson(transporter), user);
        trail.persist();

        transporter.deleted = 1;

        return transporter;
    }
}
