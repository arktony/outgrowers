package com.farm_erp.weigh_bridge.domains;

import com.farm_erp.outgrowers.domains.Permit;
import com.farm_erp.weigh_bridge.statics.WeighBridgeChannelEnums;
import com.farm_erp.weigh_bridge.statics.WeighBridgeStatusEnums;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class WeighBridgeTicket extends PanacheEntity {

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public String uniqueIdentifier;

    @Column(nullable = false)
    public String serialNumber;

    @Column(nullable = false)
    public LocalDateTime timeIn;

    public LocalDateTime timeOut;

    public Double grossWeight;

    public Double tareWeight;

    public Double compulsoryAppliedDeduction = 5.0;

    public Double additionalAppliedDeduction = 0.0;

    public String caneInspectorComment;

    public String vehicleNumber;

    public Boolean isDone = Boolean.FALSE;

    public String status = WeighBridgeStatusEnums.NEW.toString();

    public String channel = WeighBridgeChannelEnums.WEIGHBRIDGE.toString();

    @JsonbDateFormat("dd/MM/yyyy HH:mm:ss")
    public LocalDateTime entryTime = LocalDateTime.now();

    @JsonbDateFormat("dd/MM/yyyy HH:mm:ss")
    public LocalDateTime loadTime;

    @JsonbDateFormat("dd/MM/yyyy HH:mm:ss")
    public LocalDateTime unloadTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Permit permit;

    @ManyToOne
    @JoinColumn(nullable = true)
    public Token token;

    @OneToOne
    @JoinColumn(nullable = true)
    public Vehicle vehicle;

    @Transient
    public Double subTotal;

    @Transient
    public Double netWeight;

    @Transient
    public Double additionalAppliedTonneDeduction;

    @Transient
    public Double compulsoryAppliedTonneDeduction;

    public WeighBridgeTicket() {
    }

    public WeighBridgeTicket(Permit permit) {
        this.serialNumber = generateSerialNumber();
        this.timeIn = LocalDateTime.now();
        this.permit = permit;

        this.grossWeight = 0.0;
        this.tareWeight = 0.0;

        if (this.vehicleNumber != null) {
            Vehicle vehicleNo = Vehicle.findByVehicleNumber(this.vehicleNumber);
            if (vehicleNo != null) this.vehicle = vehicleNo;
        }

    }

    public Double getSubTotal() {
        return this.grossWeight - this.tareWeight;
    }

    public Double getAdditionalAppliedTonneDeduction() {
        return (this.additionalAppliedDeduction / 100) * this.getSubTotal();
    }

    public Double getCompulsoryAppliedTonneDeduction() {
        return (this.compulsoryAppliedDeduction / 100) * this.getSubTotal();
    }

    public Double getNetWeight() {
        return this.getSubTotal() - (this.getAdditionalAppliedTonneDeduction() + this.getCompulsoryAppliedTonneDeduction());
    }

    public static WeighBridgeTicket getByPermitTare(Permit permit) {
        return find("isDone=?1 and permit=?2", Boolean.FALSE, permit).firstResult();
    }

    public static WeighBridgeTicket getByIdentifier(String identifier) {
        return find("uniqueIdentifier", identifier).firstResult();
    }

    public static List<WeighBridgeTicket> findByPermit(Permit permit) {
        return list("permit", permit);
    }

    private String generateSerialNumber() {
        Optional<WeighBridgeTicket> zones = WeighBridgeTicket.findAll(Sort.by("serialNumber").ascending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = String.format("%04d", 1);
        } else {
            code = String.format("%04d", Integer.parseInt(zones.get().serialNumber) + 1);
        }

        return code;
    }
}
