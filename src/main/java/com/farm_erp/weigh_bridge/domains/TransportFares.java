package com.farm_erp.weigh_bridge.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Where(clause = "DELETED = 0")
public class TransportFares extends PanacheEntity {

    @Column(nullable = false)
    public Double fromDistance;

    public Double toDistance;

    public Boolean andAbove = Boolean.FALSE;


    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false)
    public BigDecimal cost;

    public TransportFares() {
    }

    public TransportFares(Double fromDistance, Double toDistance, Boolean andAbove, BigDecimal cost) {
        this.fromDistance = fromDistance;
        this.toDistance = toDistance;
        this.andAbove = andAbove;
        this.cost = cost;
    }

    public static TransportFares find(Double distance){
        return find("fromDistance <= ?1 and (toDistance is null or toDistance >= ?1)", distance).firstResult();
    }

    public static TransportFares findExists(Long id, Double distance){
        return find("fromDistance <= ?1 and (toDistance is null or toDistance >= ?1) and id!=?2", distance, id).firstResult();
    }
}
