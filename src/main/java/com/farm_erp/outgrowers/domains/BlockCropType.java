package com.farm_erp.outgrowers.domains;

import com.farm_erp.settings.domains.CropType;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.statics._StatusTypes_Enum;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "DELETED = 0")
public class BlockCropType extends PanacheEntity {

    @Column(nullable = false)
    public LocalDate plantingDate;

    public LocalDate actualHarvestingDate;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public Boolean isAided = Boolean.FALSE;

    public Boolean hasPermit = Boolean.FALSE;

    public LocalDateTime entryTime = LocalDateTime.now();

    public String status = _StatusTypes_Enum.ACTIVE.toString();

    @OneToMany(mappedBy = "blockCropType")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<AidBlock> aid = new ArrayList<>();

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public Block block;

    @ManyToOne
    @JoinColumn(nullable = false)
    public CropType cropType;

    @Transient
    public BigDecimal totalAid;

    @Transient
    public String age;

    @Transient
    public Double estimatedYield;

    @Transient
    public LocalDate expectedHarvestingDate;

    public BlockCropType() {
    }

    public BlockCropType(LocalDate plantingDate, Block block, CropType cropType) {
        this.plantingDate = plantingDate;
        this.block = block;
        this.cropType = cropType;
    }

    public BigDecimal getTotalAid() {
        BigDecimal total = BigDecimal.ZERO;
        for(AidBlock b : this.aid){
            total = total.add(b.getTotalAid());
        }
        return total;
    }

    public LocalDate getExpectedHarvestingDate() {
        GeneralBusinessSettings set = GeneralBusinessSettings.single(_SettingParameter_Enums.MATURITY_PERIOD.toString());
        if (set == null) throw new WebApplicationException("First set the maturity period in settings", 404);

        return this.plantingDate.plusMonths(Long.parseLong(set.settingValue));
    }

    public Double getEstimatedYield() {
        return this.cropType.expectedTonnesPerAcre * this.block.area;
    }

    public String getAge() {
        Period p = Period.between(plantingDate, Objects.requireNonNullElseGet(this.actualHarvestingDate, LocalDate::now));
        return p.getYears() + " years, " + p.getMonths() + " months, " + p.getDays() + " days";
    }
}
