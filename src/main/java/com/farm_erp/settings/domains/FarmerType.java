package com.farm_erp.settings.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "DELETED = 0")
public class FarmerType extends PanacheEntity{

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String code;

    public FarmerType() {
    }

    public FarmerType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static FarmerType find(Long id, String name, String code){
        return find("(?1 is null or id!=?1) and (name=?2 or code=?3)", id, name, code).firstResult();
    }
}
