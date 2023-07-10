package com.farm_erp.outgrowers.domains;

import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.CropType;
import com.farm_erp.settings.domains.DistrictOffice;
import com.farm_erp.settings.domains.Village;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class PermitBatch extends PanacheEntity {

    public LocalDate generationDate;

    public LocalDate issueDate;

    public String notes;

    public LocalDate startDate;

    public LocalDate endDate;

    public Integer startAgeInMonths;

    public Boolean isAided;

    public Double plantPercentage;

    public Double ratoonOnePercentage;

    public Double ratoonTwoPercentage;

    public Double ratoonThreePercentage;

    public Double totalExpectedTonnage;

    public LocalDateTime entryTime = LocalDateTime.now();

    @OneToOne
    public CaneVariety variety;

    @OneToOne
    public Village village;

    @OneToOne
    public CropType cropType;

    @OneToOne
    public DistrictOffice districtOffice;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Permit> permits;

    public PermitBatch() {
    }

    public PermitBatch(LocalDate generationDate, LocalDate issueDate, String notes, LocalDate startDate,
                       LocalDate endDate, Integer startAgeInMonths, Boolean isAided, CaneVariety variety,
                       Village village, CropType cropType, DistrictOffice districtOffice, List<Permit> permits,
                       Double plantPercentage, Double ratoonOnePercentage, Double ratoonTwoPercentage,
                       Double ratoonThreePercentage, Double totalExpectedTonnage) {
        this.generationDate = generationDate;
        this.issueDate = issueDate;
        this.notes = notes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startAgeInMonths = startAgeInMonths;
        this.isAided = isAided;
        this.variety = variety;
        this.village = village;
        this.cropType = cropType;
        this.districtOffice = districtOffice;
        this.permits = permits;
        this.plantPercentage = plantPercentage;
        this.ratoonOnePercentage = ratoonOnePercentage;
        this.ratoonTwoPercentage = ratoonTwoPercentage;
        this.ratoonThreePercentage = ratoonThreePercentage;
        this.totalExpectedTonnage = totalExpectedTonnage;
    }
}
