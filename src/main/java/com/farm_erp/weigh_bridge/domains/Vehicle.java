package com.farm_erp.weigh_bridge.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;


@Entity
@Where(clause = "DELETED = 0")
public class Vehicle extends PanacheEntity {

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Lob
    public String rfid;

    @Column(nullable = true)
    public String TIN;

    @Column(nullable = false)
    public String vehicleNumber;

    @Column(nullable = true)
    public String registrationNumber;

    @Column(nullable = true)
    public String make;

    @Column(nullable = true)
    public String color;

    @Column(nullable = true)
    public String chassisNumber;

    @Column(nullable = true)
    public String priority;

    public LocalDateTime entryTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(nullable = false)
    public Transporter transporter;

    public Vehicle() {
    }

    public Vehicle(String rfid, String TIN, String vehicleNumber, String registrationNumber, String make, String color, String chassisNumber, String priority, Transporter transporter) {
        this.rfid = rfid;
        this.TIN = TIN;
        this.vehicleNumber = vehicleNumber;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.color = color;
        this.chassisNumber = chassisNumber;
        this.priority = priority;
        this.transporter = transporter;
    }

    public static Vehicle findByRFID(String RFID){
        return find("rfid",RFID).firstResult();
    }

    public static Vehicle findByVehicleNumber(String vehicleNumber){
        return find("vehicleNumber",vehicleNumber).firstResult();
    }

}
