package com.farm_erp.accounting.domains;

import com.farm_erp.accounting.statics.PaymentStatus;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.weigh_bridge.domains.Transporter;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TransporterMoneyActivity extends PanacheEntity {

    @Column(nullable = false, precision = 26, scale = 8)
    public BigDecimal amount;

    @Column(nullable = true)
    public String description;

    public String type;

    public Long reference;

    public String status = PaymentStatus.PENDING.toString();

    public LocalDateTime entryDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(nullable = false)
    public Transporter transporter;

    public TransporterMoneyActivity() {
    }

    public TransporterMoneyActivity(BigDecimal amount, String description,String type, Long reference, Transporter transporter) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.reference = reference;
        this.transporter = transporter;
    }

    public static List<TransporterMoneyActivity> findByTransporter(Transporter transporter, LocalDate start, LocalDate end){
        return list("transporter=?1 and DATE(entryDate) between ?2 and ?3", transporter, Date.valueOf(start), Date.valueOf(end));
    }
}
