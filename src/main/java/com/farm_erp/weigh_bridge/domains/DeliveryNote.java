package com.farm_erp.weigh_bridge.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;
import com.farm_erp.outgrowers.domains.*;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class DeliveryNote extends PanacheEntity {
    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false)
    public String deliveryNoteNumber;

    public Double deliveredQuantity;

    public Double remainingQuantity;
    
    public BigDecimal transportCost;
    
    public BigDecimal payment;

    public LocalDateTime entryTime = LocalDateTime.now();

    @OneToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public WeighBridgeTicket weighBridgeTicket;

    public DeliveryNote() {
    }

    public DeliveryNote(Double deliveredQuantity, Double remainingQuantity, BigDecimal transportCost, BigDecimal payment, WeighBridgeTicket weighBridgeTicket) {
        this.deliveryNoteNumber = generateNoteNumber();
        this.deliveredQuantity = deliveredQuantity;
        this.remainingQuantity = remainingQuantity;
        this.transportCost = transportCost;
        this.payment = payment;
        this.weighBridgeTicket = weighBridgeTicket;
    }

    public static List<DeliveryNote> findByDates(LocalDate startDate, LocalDate endDate){
        return list("entryDate >= ?1 and entryDate <= ?2", startDate, endDate);
    }

    public static List<DeliveryNote> findByPermit(Permit permit){
        return list("weighBridgeTicket.permit", permit);
    }
    
    public String generateNoteNumber(){
        Optional<DeliveryNote> zones = DeliveryNote.findAll(Sort.by("deliveryNoteNumber").ascending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = String.format("%04d", 1);
        } else {
            code = String.format("%04d", Integer.parseInt(zones.get().deliveryNoteNumber) + 1);
        }

        return code;
    }
}
