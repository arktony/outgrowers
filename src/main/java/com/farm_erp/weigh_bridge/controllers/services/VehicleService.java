package com.farm_erp.weigh_bridge.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.weigh_bridge.controllers.services.payloads.PriorityRequest;
import com.farm_erp.weigh_bridge.controllers.services.payloads.RFIDRequest;
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
public class VehicleService {

    public Vehicle create(VehicleRequest request, User user) {

        Transporter transporter = Transporter.findById(request.transporterId);
        if(transporter == null) throw new WebApplicationException("Invalid transporter selected", 404);

        Vehicle vehicle = new Vehicle(
                request.rfid,
                request.TIN,
                request.vehicleNumber,
                request.registrationNumber,
                request.make,
                request.color,
                request.chassisNumber,
                request.priority.toString(),
                transporter
        );
        vehicle.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.VEHICLES.toString(), vehicle.id,
                null, jsonb.toJson(vehicle), user);
        trail.persist();


        return vehicle;
    }

    public Vehicle update(Long id, VehicleRequest request, User user) {

        Vehicle vehicle = Vehicle.findById(id);
        if (vehicle == null) throw new WebApplicationException("Invalid vehicle selected!", 404);

        Transporter transporter = Transporter.findById(request.transporterId);
        if(transporter == null) throw new WebApplicationException("Invalid transporter selected", 404);

        Vehicle oldData = vehicle;

        vehicle.TIN= request.TIN;
        vehicle.vehicleNumber=request.vehicleNumber;
        vehicle.registrationNumber=request.registrationNumber;
        vehicle.make=request.make;
        vehicle.color= request.color;
        vehicle.chassisNumber= request.chassisNumber;
        vehicle.transporter = transporter;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.VEHICLES.toString(), vehicle.id,
                jsonb.toJson(oldData), jsonb.toJson(vehicle), user);
        trail.persist();

        return vehicle;
    }

    public Vehicle changeRFID(Long id, RFIDRequest request, User user){
        Vehicle vehicle = Vehicle.findById(id);
        if (vehicle == null) throw new WebApplicationException("Invalid vehicle selected!", 404);

        Vehicle oldData = vehicle;

        vehicle.rfid = request.rfid;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.VEHICLES.toString(), vehicle.id,
                jsonb.toJson(oldData), jsonb.toJson(vehicle), user);
        trail.persist();

        return vehicle;
    }

    public Vehicle changePriority(Long id, PriorityRequest request, User user) {

        Vehicle vehicle = Vehicle.findById(id);
        if (vehicle == null) throw new WebApplicationException("Invalid vehicle selected!", 404);

        Vehicle oldData = vehicle;

        vehicle.priority = request.priority.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.VEHICLES.toString(), vehicle.id,
                jsonb.toJson(oldData), jsonb.toJson(vehicle), user);
        trail.persist();

        return vehicle;
    }

    public List<Vehicle> get() {
        return Vehicle.listAll(Sort.by("owner"));
    }

    public Vehicle delete(Long id, User user) {
        Vehicle vehicle = Vehicle.findById(id);
        if (vehicle == null) throw new WebApplicationException("Invalid vehicle selected!", 404);

        Vehicle oldData = vehicle;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.VEHICLES.toString(), vehicle.id,
                jsonb.toJson(oldData), jsonb.toJson(vehicle), user);
        trail.persist();

       vehicle.deleted =1;

        return vehicle;
    }
}
