package com.farm_erp.outgrowers.domains;

import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.CropType;
import com.farm_erp.outgrowers.domains.FarmerData;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.statics._StatusTypes_Enum;

import javax.persistence.Column;

public class BlockDataSummary {
    public Long id;

    public String blockNumber;

    public Double area;

    public Double distance;

    public String status;

    public FarmerData farmer;

    public Village village;

    public CaneVariety caneVariety;

    public BlockDataSummary() {
    }

    public BlockDataSummary(Long id, String blockNumber, Double area, Double distance, String status, FarmerData farmer, Village village, CaneVariety caneVariety) {
        this.id = id;
        this.blockNumber = blockNumber;
        this.area = area;
        this.distance = distance;
        this.status = status;
        this.farmer = farmer;
        this.village = village;
        this.caneVariety = caneVariety;
    }
}
