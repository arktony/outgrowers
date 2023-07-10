package com.farm_erp.outgrowers.domains;

import com.farm_erp.statics._StatusTypes_Enum;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
public class Permit extends PanacheEntity {

    @Column(nullable = false)
    public String validityPeriod;

    @Column(nullable = false)
    public LocalDate generationDate = LocalDate.now();

    public LocalDate issueDate;

    public Boolean isIssued = Boolean.FALSE;

    @Column(nullable = false)
    public LocalDate expiryDate;

    @Column(nullable = false)
    public String serialNumber;

    @Column(nullable = false)
    public Double estimatedYield;

    public Double deliveredYield = 0.0;

    public LocalDateTime entryTime = LocalDateTime.now();

    public String status = _StatusTypes_Enum.ACTIVE.toString();

    public Boolean isExtended = Boolean.FALSE;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public Block block;

    @Transient
    public BlockDataSummary blockDataSummary;

    @Transient
    public FarmerData farmerData;

    @Transient
    public Double remainingYield;

    public Permit() {
    }

    public Permit(String validityPeriod, LocalDate generationDate, LocalDate expiryDate, Double estimatedYield, Block block) {
        this.validityPeriod = validityPeriod;
        this.serialNumber = generateSerialNumber();
        this.generationDate = generationDate;
        this.expiryDate = expiryDate;
        this.estimatedYield = estimatedYield;
        this.block = block;
    }

    public FarmerData getFarmerData() {
        return this.block.getFarmerDetails();
    }

    public BlockDataSummary getBlockDataSummary() {
        BlockDataSummary sum = new BlockDataSummary();
        sum.blockNumber = this.block.blockNumber;
        sum.area = this.block.area;
        sum.distance = this.block.distance;
        sum.status = this.block.status;
        sum.farmer = this.block.farmerDetails;
        sum.village = this.block.village;
        sum.caneVariety = this.block.caneVariety;

        return sum;
    }

    public Double getRemainingYield() {
        return this.estimatedYield - this.deliveredYield;
    }

    public static Permit findBySerialNumber(String serialNumber) {
        return find("serialNumber", serialNumber).firstResult();
    }

    public static List<Permit> findPermits(String status, Boolean isIssued, Boolean isExtended) {
        return list("(?1 is null or status=?1) and (?2 is null or isIssued=?2) and (?3 is null or isExtended=?3)", status, isIssued, isExtended);
    }

    public static List<Permit> findPermitsByBlock(Block block) {
        return list("block", block);
    }

    private String generateSerialNumber() {
        Optional<Permit> zones = Permit.findAll(Sort.by("serialNumber").descending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = String.format("%04d", 1);
        } else {
            code = String.format("%04d", Integer.parseInt(zones.get().serialNumber) + 1);
        }

        return code;
    }
}
