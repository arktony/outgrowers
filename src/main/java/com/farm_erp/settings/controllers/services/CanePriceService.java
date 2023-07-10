package com.farm_erp.settings.controllers.services;

import com.farm_erp.outgrowers.domains.PermitBatch;
import com.farm_erp.settings.domains.CanePrice;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CanePriceService {

    public List<CanePrice> fetch(LocalDate startDate, LocalDate endDate){
        if (startDate == null)
            startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null)
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return CanePrice.list("entryTime between ?1 and ?2", startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }
}
