package com.farm_erp.outgrowers.domains;

import java.math.BigDecimal;

public class BlockAidData {
    public String blockNumber;
    public String village;
    public String farmerName;
    public String registrationNumber;
    public String status;
    public BigDecimal totalAid = BigDecimal.ZERO;
    public BigDecimal paidTotalAid = BigDecimal.ZERO;

}
