package com.farm_erp.outgrowers.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.outgrowers.controllers.services.payloads.AidBlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockAidddRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockVerifyRequest;
import com.farm_erp.outgrowers.domains.AidBlock;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.settings.domains.Aid;
import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.District;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class AidBlockService {

    public void create(Long id, BlockAidddRequest requests, User user) {
        BlockCropType type = BlockCropType.findById(id);
        if(type == null) throw new WebApplicationException("Invalid block cycle selected",404);

        for (AidBlockRequest req : requests.data){
            Aid aid = Aid.findById(req.aidId);
            if(aid == null) throw new WebApplicationException("Invalid aid selected",404);

            type.block.getActiveRatoon().isAided = Boolean.TRUE;

            AidBlock block = new AidBlock(Double.valueOf(req.numberOfAcres), aid, type);
            if(req.amount != null){
                block.amount = req.amount;
            }
            block.persist();

            Jsonb jsonb = JsonbBuilder.create();
            AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.AID_BLOCK.toString(), block.id,
                    null, jsonb.toJson(block), user);
            trail.persist();
        }
    }

    public AidBlock update(Long id, AidBlockRequest request, User user) {

        AidBlock block = AidBlock.findById(id);
        if (block == null) throw new WebApplicationException("Invalid aid block selected!", 404);

        Aid aid = Aid.findById(request.aidId);
        if(aid == null) throw new WebApplicationException("Invalid aid selected",404);


        AidBlock oldData = block;

        block.numberOfAidedAcres = Double.valueOf(request.numberOfAcres);
        block.aid = aid;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.AID_BLOCK.toString(), block.id,
                jsonb.toJson(oldData), jsonb.toJson(block), user);
        trail.persist();

        return block;
    }

    public AidBlock delete(Long id, User user) {
        AidBlock block = AidBlock.findById(id);
        if (block == null) throw new WebApplicationException("Invalid aid block selected!", 404);

        AidBlock oldData = block;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.AID_BLOCK.toString(), block.id,
                jsonb.toJson(oldData), jsonb.toJson(block), user);
        trail.persist();

        block.deleted = 1;

        return block;
    }
}
