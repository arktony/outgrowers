package com.farm_erp.payments.domains;

import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.outgrowers.domains.FarmerData;
import com.farm_erp.payments.statics.TransactionType;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.weigh_bridge.domains.Transporter;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Where(clause = "DELETED = 0")
public class Transaction extends PanacheEntity {

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false, precision = 26, scale = 8)
    public BigDecimal amount;

    public String status = _StatusTypes_Enum.PENDING.toString();

    public LocalDateTime entryDate = LocalDateTime.now();

    public String transactionType;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = true)
    public Farmer farmer;

    @Transient
    public FarmerData farmerData;

    @ManyToOne
    @JoinColumn(nullable = true)
    public Transporter transporter;

    public Transaction() {
    }

    public Transaction(BigDecimal amount, TransactionType type, Farmer farmer, Transporter transporter) {
        this.amount = amount;
        this.transactionType = type.toString();
        this.farmer = farmer;
        this.transporter = transporter;
    }

    public FarmerData getFarmerData() {
        if (this.farmer != null)
            return new FarmerData(this.farmer.id, this.farmer.registrationNumber, this.farmer.firstName, this.farmer.surName, this.farmer.otherName);

        return null;
    }
}
