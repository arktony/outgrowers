package com.farm_erp.settings.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Where(clause = "DELETED = 0")
public class CropType extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String code;

    @Column(nullable = false)
    public Double expectedTonnesPerAcre;

    @Column(nullable = false)
    public Integer position;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public LocalDateTime entryTime = LocalDateTime.now();

    public CropType() {
    }

    public CropType(String name, String code, Double expectedTonnesPerAcre, Integer position) {
        this.name = name;
        this.code = code;
        this.expectedTonnesPerAcre = expectedTonnesPerAcre;
        this.position = position;
    }

    public static CropType findByName(String name){
        return find("name", name).firstResult();
    }

    public static CropType findByCode(String code){
        return find("code", code).firstResult();
    }

    public static CropType findByPosition(Integer position){
        return find("position", position).firstResult();
    }

    public static CropType findByNameExists(String name, Long id){
        return find("name=?1 and id!=?2", name,id).firstResult();
    }

    public static CropType findByCodeExists(String code, Long id){
        return find("code=?1 and id!=?2", code,id).firstResult();
    }

    public static CropType findByPositionExists(Integer position, Long id){
        return find("position=?1 and id!=?2", position,id).firstResult();
    }
}
