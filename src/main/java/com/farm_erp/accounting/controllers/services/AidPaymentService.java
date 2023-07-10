package com.farm_erp.accounting.controllers.services;

import com.farm_erp.accounting.controllers.services.payloads.AidMoney;
import com.farm_erp.accounting.domains.AidPaymentActivity;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AidPaymentService {
    public List<AidMoney> getByBlock(Long id){

        List<AidMoney> data = new ArrayList<>();

        Block block = Block.findById(id);
        if(block == null) throw new WebApplicationException("Invalid block selected",404);

        List<BlockCropType> types = BlockCropType.list("block", block);
        for(BlockCropType type : types){
            AidMoney mon = new AidMoney(type.cropType.name, type.plantingDate, type.actualHarvestingDate,type.status,type.getTotalAid());
            List<AidPaymentActivity> acts = AidPaymentActivity.findByBlockCycle(type);

            BigDecimal total = BigDecimal.ZERO;
            for(AidPaymentActivity act: acts){
                total = total.add(act.amount);
            }

            mon.totalPaidAid = total;
            mon.totalBalance = type.getTotalAid().subtract(total);
            mon.payments = acts;

            data.add(mon);
        }

        return data;
    }

}
