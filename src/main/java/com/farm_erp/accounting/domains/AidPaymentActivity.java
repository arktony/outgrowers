package com.farm_erp.accounting.domains;

import com.farm_erp.accounting.statics.PaymentStatus;
import com.farm_erp.outgrowers.domains.AidBlock;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;
import com.farm_erp.outgrowers.domains.BlockDataSummary;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class AidPaymentActivity extends PanacheEntity {

    @Column(nullable = false, precision = 26, scale = 8)
    public BigDecimal amount;

    @Column(nullable = true)
    public String description;

    public String status = PaymentStatus.PAID.toString();

    public LocalDateTime entryDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonbTransient
    public BlockCropType blockCropType;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public Block block;


    public AidPaymentActivity() {
    }

    public AidPaymentActivity(BigDecimal amount, String description, BlockCropType blockCropType) {
        this.amount = amount;
        this.description = description;
        this.blockCropType = blockCropType;
        this.block = blockCropType.block;
    }

    public static List<AidPaymentActivity> findByBlockCycle(BlockCropType blockCropType){
        return list("blockCropType", blockCropType);
    }

    public static List<AidPaymentActivity> findByBlock(Block block){
        return list("block", block);
    }

}
