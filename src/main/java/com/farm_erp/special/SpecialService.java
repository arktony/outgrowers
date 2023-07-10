package com.farm_erp.special;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.outgrowers.domains.Permit;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.weigh_bridge.domains.WeighBridgeTicket;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class SpecialService {

    public void rectifyCodes() {
        List<Farmer> farmers = Farmer.listAll(Sort.by("id"));
        int i = 0;
        for (Farmer f : farmers) {
            f.registrationNumber = "GM" + String.format("%01d", i + 1);
            i++;
        }
    }

    public void rectifyBlockCodes() {
        List<Farmer> farmers = Farmer.listAll(Sort.by("id"));

        for (Farmer fr : farmers) {
            List<Block> blocks = Block.searchByFarmer(fr);
            int j = 1;
            for (Block f : blocks) {
                f.blockNumber = fr.registrationNumber + "/" + f.district.code + "/" + f.districtOffice.code + "/"
                        + f.village.code + "/" + j;
                j++;
            }
        }

    }

    public void delete(Long id) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        List<Block> blocks = Block.searchByFarmer(farmer);
        farmer.delete();
        blocks.forEach(block -> {
            List<Permit> permits = Permit.findPermitsByBlock(block);
            permits.forEach(permit -> {
                List<WeighBridgeTicket> ticks = WeighBridgeTicket.findByPermit(permit);
                ticks.forEach(PanacheEntityBase::delete);
            });
        });

    }

    public void rectifyVillageCodes() {
        List<Village> villages = Village.listAll(Sort.by("id"));
        int j = 1;
        for(Village v : villages){
            v.code = v.district.code+j;
            j++;
        }
    }
}
