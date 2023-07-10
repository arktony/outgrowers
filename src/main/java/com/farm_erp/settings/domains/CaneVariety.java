package com.farm_erp.settings.domains;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Where(clause = "DELETED = 0")
public class CaneVariety extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = true)
    public String description;

    public LocalDateTime entryTime = LocalDateTime.now();

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public CaneVariety() {
    }

    public CaneVariety(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static CaneVariety findByName(String name){
        return find("name", name).firstResult();
    }

    public static CaneVariety findByNameExists(String name, Long id){
        return find("name=?1 and id!=?2", name, id).firstResult();
    }
}

