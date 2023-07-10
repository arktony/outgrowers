package com.farm_erp.accounting.domains;

import com.farm_erp.accounting.statics.PaymentStatus;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.outgrowers.domains.FarmerData;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;

@Entity
public class FarmerMoneyActivity extends PanacheEntity {

    @Column(nullable = false, precision = 26, scale = 8)
    public BigDecimal amount;

    @Column(nullable = true)
    public String description;

    public String type;

    public Long reference;

    public LocalDateTime entryDate = LocalDateTime.now();

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public Farmer farmer;

    @Transient
    public FarmerData farmerData;

    public FarmerMoneyActivity() {
    }

    public FarmerMoneyActivity(BigDecimal amount, String description, String type, Long reference, Farmer farmer) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.reference = reference;
        this.farmer = farmer;
    }

    public FarmerData getFarmerData() {
        return new FarmerData(this.farmer.id, this.farmer.registrationNumber, this.farmer.firstName, this.farmer.surName, this.farmer.otherName);
    }

    public static List<FarmerMoneyActivity> findByFarmer(Farmer farmer, LocalDate start, LocalDate end){
        return list("farmer=?1 and DATE(entryDate) between ?2 and ?3", farmer, Date.valueOf(start), Date.valueOf(end));
    }

}
