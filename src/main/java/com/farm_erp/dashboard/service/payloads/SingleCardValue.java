package com.farm_erp.dashboard.service.payloads;

public class SingleCardValue {
    public String name;
    public Double value;
    public Double change;

    public SingleCardValue() {
    }

    public SingleCardValue(String name, Double value, Double change) {
        this.name = name;
        this.value = value;
        this.change = change;
    }

}
