package com.farm_erp.outgrowers.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockVerifyRequest;
import com.farm_erp.outgrowers.domains.*;
import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.District;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class BlockService {

    public Block create(BlockRequest req, User user) {

        Farmer farmer = Farmer.findById(req.farmerId);
        if (farmer == null) throw new WebApplicationException("Invalid farmer selected", 404);

        Village village = Village.findById(req.villageId);
        if (village == null) throw new WebApplicationException("Invalid village selected", 404);

        CaneVariety variety = CaneVariety.findById(req.caneVarietyId);
        if (variety == null) throw new WebApplicationException("Invalid variety selected", 404);

        Block block = new Block(req.area, req.distance, req.landOwnership, farmer, village, variety);
        block.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.BLOCK.toString(), block.id,
                null, jsonb.toJson(block), user);
        trail.persist();

        return block;
    }

    public Block update(Long id, BlockRequest request, User user) {

        Block block = Block.findById(id);
        if (block == null) throw new WebApplicationException("Invalid block selected!", 404);

        Farmer farmer = Farmer.findById(request.farmerId);
        if (farmer == null) throw new WebApplicationException("Invalid farmer selected", 404);

        CaneVariety variety = CaneVariety.findById(request.caneVarietyId);
        if (variety == null) throw new WebApplicationException("Invalid variety selected", 404);

        Village village = Village.findById(request.villageId);
        if (village == null) throw new WebApplicationException("Invalid village selected", 404);

        Block oldData = block;

        block.area = request.area;
        block.landOwnership = request.landOwnership;
        block.distance = request.distance;
        block.farmer = farmer;
        block.caneVariety = variety;
        block.village = village;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.BLOCK.toString(), block.id,
                jsonb.toJson(oldData), jsonb.toJson(block), user);
        trail.persist();

        return block;
    }

    public Block verify(Long id, BlockVerifyRequest request, User user) {

        Block block = Block.findById(id);
        if (block == null) throw new WebApplicationException("Invalid block selected!", 404);

        Block oldData = block;

        block.status = _StatusTypes_Enum.VERIFIED.toString();
        block.supervisor = request.supervisor;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.VERIFIED.toString(), _Section_Enums.BLOCK.toString(), block.id,
                jsonb.toJson(oldData), jsonb.toJson(block), user);
        trail.persist();

        return block;
    }


    public Block getDetails(Long id) {
        Block block = Block.findById(id);
        if (block == null) throw new WebApplicationException("Invalid block selected!", 404);

        return block;
    }

    public List<Block> get(Long districtId, Long villageId) {
        District district = null;
        if (districtId != null) {
            district = District.findById(districtId);
            if (district == null) throw new WebApplicationException("Invalid zone selected", 404);
        }

        Village village = null;
        if (villageId != null) {
            village = Village.findById(villageId);
            if (village == null) throw new WebApplicationException("Invalid village selected", 404);
        }

        return Block.search(district, village);
    }

    public Block delete(Long id, User user) {
        Block block = Block.findById(id);
        if (block == null) throw new WebApplicationException("Invalid block selected!", 404);

        Block oldData = block;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.BLOCK.toString(), block.id,
                jsonb.toJson(oldData), jsonb.toJson(block), user);
        trail.persist();

        block.deleted = 1;

        return block;
    }
}
