package com.farm_erp.settings.domains;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Where(clause = "DELETED = 0")
public class Aid extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = true)
    public String description;

    public LocalDateTime entryTime = LocalDateTime.now();

    public BigDecimal costPerAcre;

    public Boolean isIndependent = Boolean.FALSE;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public Aid() {
    }

    public Aid(String name, String description, BigDecimal costPerHectare, Boolean isIndependent) {
        this.name = name;
        this.description = description;
        this.costPerAcre = costPerHectare;
        this.isIndependent = isIndependent;
    }

    public static Aid findByName(String name) {
        return find("name", name).firstResult();
    }

    public static Aid findByNameExists(String name, Long id) {
        return find("name=?1 and id!=?2", name, id).firstResult();
    }
}

