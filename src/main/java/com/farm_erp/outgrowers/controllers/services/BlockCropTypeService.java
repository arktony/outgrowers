package com.farm_erp.outgrowers.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.controllers.services.payloads.BlockCropTypeRequest;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;
import com.farm_erp.settings.domains.CropType;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class BlockCropTypeService {

    public BlockCropType create(BlockCropTypeRequest request, User user){
        Block block = Block.findById(request.blockId);
        if(block == null) throw new WebApplicationException("Invalid block selected",404);

        if(block.hasActivePermit) throw new WebApplicationException("Block has a running permit.", 406);

        CropType type = CropType.findById(request.cropTypeId);
        if(type == null) throw new WebApplicationException("Invalid crop type selected", 404);

        BlockCropType ctype = new BlockCropType(request.plantingDate, block, type);
        ctype.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.BLOCK_CYCLE.toString(), ctype.id,
                null, jsonb.toJson(ctype), user);
        trail.persist();

        return ctype;
    }
}
