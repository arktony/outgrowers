package com.farm_erp.outgrowers.domains;

public class FarmerData {
    public Long id;
    public String code;
    public String firstName;
    public String lastName;
    public String otherName;

    public FarmerData() {
    }

    public FarmerData(Long id, String code, String firstName, String lastName, String otherName) {
        this.id = id;
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherName = otherName;
    }

}
