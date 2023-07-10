package com.farm_erp.accounting.controllers.services.payloads;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransporterMoney {
    public BigDecimal amount;

    public String description;

    public String type;

    public LocalDate date;

    public BigDecimal balance;

    public TransporterMoney() {
    }

    public TransporterMoney(BigDecimal amount, String description, String type, LocalDate date, BigDecimal balance) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = date;
        this.balance = balance;
    }
}
