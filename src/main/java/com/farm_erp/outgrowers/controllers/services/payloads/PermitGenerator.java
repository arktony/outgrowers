package com.farm_erp.outgrowers.controllers.services.payloads;

public class PermitGenerator {
    public Long id;
    public String blockNumber;
    public String farmer;
    public String village;
    public String districtOffice;
    public String phoneNumbers;
    public Double estimatedYield;
    public String expectedHarvestingDate;
    public String currentAge;
    public String cropType;

    public PermitGenerator() {
    }

    public PermitGenerator(Long id, String blockNumber, String farmer, String village, String districtOffice, String phoneNumbers, Double estimatedYield, String expectedHarvestingDate, String currentAge, String cropType) {
        this.id = id;
        this.blockNumber = blockNumber;
        this.farmer = farmer;
        this.village = village;
        this.districtOffice = districtOffice;
        this.phoneNumbers = phoneNumbers;
        this.estimatedYield = estimatedYield;
        this.expectedHarvestingDate = expectedHarvestingDate;
        this.currentAge = currentAge;
        this.cropType=cropType;
    }
}
