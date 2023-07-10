package com.farm_erp.outgrowers.domains;

import com.farm_erp.settings.domains.*;
import com.farm_erp.statics._StatusTypes_Enum;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class Block extends PanacheEntity {

    public Integer nextCount;

    @Column(nullable = false)
    public String blockNumber;

    @Column(nullable = false)
    public Double area;

    @Column(nullable = false)
    public Double distance;

    public String status = _StatusTypes_Enum.PENDING.toString();

    @Column(nullable = false)
    public String landOwnership;

    public LocalDateTime entryTime = LocalDateTime.now();

    public Boolean hasActivePermit = Boolean.FALSE;

    public String supervisor;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @OneToMany(mappedBy = "block")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<BlockCropType> blockCropTypes = new ArrayList<>();

    @OneToMany(mappedBy = "block")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Permit> permits = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    public Farmer farmer;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Village village;

    @ManyToOne
    @JoinColumn(nullable = false)
    public CaneVariety caneVariety;

    @ManyToOne
    @JoinColumn(nullable = false)
    public District district;

    @ManyToOne
    @JoinColumn(nullable = false)
    public DistrictOffice districtOffice;

    @Transient
    public BlockCropType activeRatoon;

    @Transient
    public FarmerData farmerDetails;

    @Transient
    public BigDecimal totalAid;

    @Transient
    public Double standingCane;

    @Transient
    public Boolean isAided;

    @Transient
    public Permit activePermit;

    public Block() {
    }

    public Block(Double area, Double distance, String landOwnership, Farmer farmer, Village village, CaneVariety caneVariety) {
        this.nextCount = getCount(farmer);
        this.area = area;
        this.distance = distance;
        this.landOwnership = landOwnership;
        this.farmer = farmer;
        this.village = village;
        this.district = village.district;
        this.districtOffice = this.district.districtOffice;
        this.blockNumber = generateBlockNumber(farmer, village, district, districtOffice, this.nextCount);
        this.caneVariety = caneVariety;
    }

    public static List<Block> search(District district, Village village) {
        return list("(?1 is null or district=?1) and (?2 is null or village=?2)", Sort.by("id"), district, village);
    }

    public static List<Block> searchByFarmer(Farmer farmer) {
        return list("farmer", Sort.by("id"), farmer);
    }

    public static List<Block> searchByVarietyCropType(CaneVariety variety, CropType cropType) {
        List<Block> blocks = list("(?1 is null or caneVariety=?1)", variety);
        blocks.removeIf(b -> b.getActiveRatoon() == null);
        if (cropType != null) {
            blocks.removeIf(b -> b.getActiveRatoon().cropType != cropType);
        }
        return blocks;

    }

    public Permit getActivePermit() {
        Permit permit = null;
        if (this.hasActivePermit) {
            for (Permit p : this.permits) {
                if (p.status.equals(_StatusTypes_Enum.ACTIVE.toString())) permit = p;
            }
        }
        return permit;
    }

    public BlockCropType getActiveRatoon() {
        BlockCropType cycle = null;
        for (BlockCropType c : this.blockCropTypes) {
            if (c.status.equals(_StatusTypes_Enum.ACTIVE.toString())) {
                cycle = c;
            }
        }
        return cycle;
    }

    public Boolean getIsAided() {
        BlockCropType cycle = this.getActiveRatoon();
        if (cycle == null) {
            return Boolean.FALSE;
        } else {
            return cycle.isAided;
        }
    }

    public Double getStandingCane() {
        if (this.getActiveRatoon() != null)
            return this.getActiveRatoon().estimatedYield;

        return 0.0;
    }

    public BigDecimal getTotalAid() {
        BigDecimal aid = BigDecimal.ZERO;
        for (BlockCropType block : this.blockCropTypes) {
            if (block.isAided) {
                aid = aid.add(block.getTotalAid());
            }
        }
        return aid;
    }

    public FarmerData getFarmerDetails() {
        return new FarmerData(this.farmer.id, this.farmer.registrationNumber, this.farmer.firstName, this.farmer.surName, this.farmer.otherName);
    }

    private int getCount(Farmer farmer) {
        List<Block> blocks = Block.list("farmer",Sort.by("id").descending(), farmer);
        if (blocks.isEmpty()) return 1;

        return blocks.get(0).nextCount + 1;
    }

    private String generateBlockNumber(Farmer farmer, Village village, District district, DistrictOffice districtOffice, Integer count) {
        return farmer.registrationNumber + "/" + district.code + "/" + districtOffice.code + "/" + village.code + "/" + count;
    }
}
