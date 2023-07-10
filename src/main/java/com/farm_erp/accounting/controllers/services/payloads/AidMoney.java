package com.farm_erp.accounting.controllers.services.payloads;

import com.farm_erp.accounting.domains.AidPaymentActivity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AidMoney {

    public String cropType;

    public LocalDate plantingDate;

    public LocalDate actualHarvestingDate;

    public String status;

    public BigDecimal totalAid;

    public BigDecimal totalPaidAid;

    public BigDecimal totalBalance;

    public List<AidPaymentActivity> payments = new ArrayList<>();

    public AidMoney() {
    }

    public AidMoney(String cropType, LocalDate plantingDate, LocalDate actualHarvestingDate, String status, BigDecimal totalAid) {
        this.cropType = cropType;
        this.plantingDate = plantingDate;
        this.actualHarvestingDate = actualHarvestingDate;
        this.status = status;
        this.totalAid = totalAid;
    }
}
