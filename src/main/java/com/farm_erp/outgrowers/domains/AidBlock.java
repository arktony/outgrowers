package com.farm_erp.outgrowers.domains;

import com.farm_erp.settings.domains.Aid;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Where(clause = "DELETED = 0")
public class AidBlock extends PanacheEntity {

    @Column(nullable = true)
    public Double numberOfAidedAcres;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public BigDecimal amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Aid aid;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public BlockCropType blockCropType;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public Block block;

    @Transient
    public BigDecimal totalAid;

    public AidBlock() {
    }

    public AidBlock(Double numberOfAidedAcres, Aid aid, BlockCropType blockCropType) {
        this.numberOfAidedAcres = numberOfAidedAcres;
        this.aid = aid;
        this.blockCropType = blockCropType;
        this.block = blockCropType.block;
    }

    public BigDecimal getTotalAid() {
        return new BigDecimal(this.numberOfAidedAcres).multiply(this.aid.costPerAcre);
    }
}
