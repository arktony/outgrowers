package com.farm_erp.weigh_bridge.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.weigh_bridge.controllers.services.payloads.TransportFaresRequest;
import com.farm_erp.weigh_bridge.domains.TransportFares;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class TransportFareService {

    public TransportFares create(TransportFaresRequest request, User user) {

        if (Boolean.TRUE.equals(request.andAbove) && request.toDistance != null)
            throw new WebApplicationException("Either select andAbove boolean or toDistance but not both", 403);

        TransportFares e1 = TransportFares.find(request.fromDistance);
        if(e1 != null) throw new WebApplicationException("The from distance value overlaps with another fare",406);

        TransportFares e2 = TransportFares.find(request.toDistance);
        if(e2 != null) throw new WebApplicationException("The to distance value overlaps with another fare",406);

        TransportFares fare = new TransportFares(
                request.fromDistance,
                request.toDistance,
                request.andAbove,
                request.cost
                
        );
        fare.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.TRANSPORTFARES.toString(), fare.id,
                null, jsonb.toJson(fare), user);
        trail.persist();


        return fare;
    }

    public TransportFares update(Long id, TransportFaresRequest request, User user) {

        if (request.andAbove && request.toDistance != null)
            throw new WebApplicationException("Either select andAbove boolean or toDistance but not both", 403);

        TransportFares fare = TransportFares.findById(id);
        if (fare == null) throw new WebApplicationException("Invalid fare selected!", 404);

        TransportFares e1 = TransportFares.findExists(id, request.fromDistance);
        if(e1 != null) throw new WebApplicationException("The from distance value overlaps with another fare",406);

        TransportFares e2 = TransportFares.findExists(id, request.toDistance);
        if(e2 != null) throw new WebApplicationException("The to distance value overlaps with another fare",406);

        TransportFares oldData = fare;

        fare.fromDistance = request.fromDistance;
        fare.toDistance = request.toDistance;
        fare.andAbove=request.andAbove;
        fare.cost=request.cost;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.TRANSPORTFARES.toString(), fare.id,
                jsonb.toJson(oldData), jsonb.toJson(fare), user);
        trail.persist();

        return fare;
    }

    public List<TransportFares> get() {
        return TransportFares.listAll(Sort.by("cost"));
    }

    public TransportFares delete(Long id, User user) {
        TransportFares fare = TransportFares.findById(id);
        if (fare == null) throw new WebApplicationException("Invalid fare selected!", 404);

        TransportFares oldData = fare;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.TRANSPORTFARES.toString(), fare.id,
                jsonb.toJson(oldData), jsonb.toJson(fare), user);
        trail.persist();

        fare.deleted = 1;

        return fare;
    }
}
